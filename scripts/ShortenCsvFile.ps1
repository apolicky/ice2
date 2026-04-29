[CmdletBinding()]
param (
    [ValidateScript({ Test-Path $_ })]
    [string]
    $CsvInputFile,
    
    [string]
    $CsvOutputFile,

    [Parameter(Mandatory = $true)]
    [int]
    $NumLines,
    
    [int]
    [ValidateScript({ $_ -ge 0 })]
    $SkipEachXthLine = 0
)

[long] $lineCounter = 0;

$takeAtMost = $NumLines * (1 + $SkipEachXthLine)

Get-Content $CsvInputFile | ConvertFrom-Csv -Delimiter "," | Select-Object -First $takeAtMost | ForEach-Object {
    $lineCounter++
    if ($lineCounter % (1 + $SkipEachXthLine) -eq 0) {
        $_
    }

} | Export-Csv -Path $CsvOutputFile -Delimiter ',' -UseQuotes AsNeeded
