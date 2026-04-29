# Copilot generated script to run Cypher queries and collect metrics. Update the password parameter before running.
# RunCypherQueries.ps1
# Script to run all Cypher queries in the rules directory against Neo4j and collect metrics

param(
    [string]$Neo4jUrl = "http://localhost:7474",
    [string]$Database = "neo4j",
    [string]$Username = "neo4j",
    [string]$Password = "your_password"  # Update this with the actual password
)

$txUrl = "$Neo4jUrl/db/$Database/tx/commit"

$rulesDir = Join-Path $PSScriptRoot "..\rules"
$cypherFiles = Get-ChildItem -Path $rulesDir -Filter "r*.cypher" | Where-Object { $_.Name -ne "rules.cypher" }

$results = @()

foreach ($file in $cypherFiles) {
    $query = Get-Content -Path $file.FullName -Raw
    $query = $query.Trim()

    if ([string]::IsNullOrEmpty($query)) {
        Write-Host "Skipping empty query in $($file.Name)"
        continue
    }

    $body = @{
        statements = @(
            @{
                statement = $query
            }
        )
    } | ConvertTo-Json -Depth 10

    $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()

    try {
        $response = Invoke-WebRequest -Uri $txUrl -Method Post -Body $body -ContentType "application/json" -Headers @{
            Authorization = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("$Username`:$Password"))
        }

        $stopwatch.Stop()
        $elapsedMs = $stopwatch.ElapsedMilliseconds

        $jsonResponse = $response.Content | ConvertFrom-Json

        if ($jsonResponse.errors -and $jsonResponse.errors.Count -gt 0) {
            Write-Host "Error in $($file.Name): $($jsonResponse.errors[0].message)"
            $recordCount = 0
        }
        else {
            $recordCount = 0
            if ($jsonResponse.results -and $jsonResponse.results.Count -gt 0) {
                $recordCount = $jsonResponse.results[0].data.Count
            }
        }

        $results += [PSCustomObject]@{
            FileName    = $file.Name
            RecordCount = $recordCount
            TimeMs      = $elapsedMs
        }

        Write-Host "$($file.Name): $recordCount records, ${elapsedMs}ms"

    }
    catch {
        $stopwatch.Stop()
        Write-Host "Failed to run $($file.Name): $($_.Exception.Message)"
        $results += [PSCustomObject]@{
            FileName    = $file.Name
            RecordCount = 0
            TimeMs      = $stopwatch.ElapsedMilliseconds
        }
    }
}

# Output results to CSV
$results | Export-Csv -Path (Join-Path $PSScriptRoot "cypher_results.csv") -NoTypeInformation

Write-Host "Results saved to cypher_results.csv"