[CmdletBinding()]
param (
    [ValidateScript({ Test-Path $_ })]
    [string]
    $CsvInputFile__ImdbTitle,
    
    [string]
    $TsvInputFile,

    [string]
    $CsvOutputFile,
    
    [string]
    $NullValue = $null,

    [string[]]
    $SkipColumns = @(),

    [switch]
    $OnlyKnownMovies = $false,

    [string]
    $TitleIdHeader = 'tconst',
    
    [switch]
    $CountSeenMovies = $false
)

if ($OnlyKnownMovies) {
    [string[]] $movieIds = Import-Csv -Path $CsvInputFile__ImdbTitle | ForEach-Object {
        $_.tconst
    }
    $setOfMovieIds = [System.Collections.Generic.HashSet[string]]::new($movieIds)
    Write-Host "Number of movie ids: $($setOfMovieIds.Count)"
}


$setOfSeenMovieIds = [System.Collections.Generic.HashSet[string]]::new()
[long] $lineCounterWhere = 0;

Import-Csv -Path $TsvInputFile -Delimiter "`t"  | Where-Object {
    $lineCounterWhere++
    if ($OnlyKnownMovies) {
        $tconst = $_.PSObject.Properties[$TitleIdHeader].Value;
        $contains = $setOfMovieIds.Contains($tconst)
        if ($CountSeenMovies) {
            if ($contains) {
                $setOfSeenMovieIds.Add($tconst) | Out-Null
            }
            if ($lineCounterWhere % 5000 -eq 0) {
                Write-Host "Read $lineCounterWhere lines in where-object, seen movie ids: $($setOfSeenMovieIds.Count)"
            }
        }
        $contains
    }
    else {
        $true
    }
} | ForEach-Object {
    $oldObj = $_
    $properties = $oldObj.PSObject.Properties
    foreach ($property in $properties) {
        $columnName = $property.Name
        $oldValue = $property.Value
        if ($SkipColumns -contains $columnName) {
            $oldObj = $oldObj | Select-Object -Property * -ExcludeProperty $columnName
        }
        if (![string]::IsNullOrEmpty($NullValue) -and $oldValue -eq $NullValue) {
            Add-Member -InputObject $oldObj -MemberType NoteProperty -Name $columnName -Value $null -Force
        }
    }
    $oldObj
} | Export-Csv -Path $CsvOutputFile -Delimiter ',' 
