param (
    [string]
    $JsonInputFile,

    [int]
    $NumLines = 10000,

    [string]
    $CsvOutputFile,

    [string[]]
    $SkipColumns = @()
)

((Get-Content $JsonInputFile -Head $NumLines) | ConvertFrom-Json) | ForEach-Object {
    $oldObj = $_
    $properties = $oldObj.PSObject.Properties
    foreach ($property in $properties) {
        $columnName = $property.Name
        if ($SkipColumns -contains $columnName) {
            $oldObj = $oldObj | Select-Object -Property * -ExcludeProperty $columnName
        }
    }
    $oldObj
} | Export-Csv -Path $CsvOutputFile -Delimiter ',' 

