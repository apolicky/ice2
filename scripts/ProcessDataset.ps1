[CmdletBinding()]
param (
    [Parameter(Mandatory)]
    [ValidateScript({ Test-Path $_ -PathType Container })]
    [string]
    $CsvDirectory,

    [Parameter(Mandatory)]
    [string]
    $OutputDirectory,

    [string]
    $FilenamePrefix = "Modified_",

    [string]
    $NullValue = $null,

    [switch]
    $SkipColumnNameRewrite = $false,
    
    [switch]
    $ReplaceDateType_1998_01_01__to_1998_01_01T00_00_00 = $false
)

$csvs = Get-ChildItem -Path $CsvDirectory

foreach ($csv in $csvs) {
    $csv | Import-Csv -Delimiter ',' | ForEach-Object {
        $oldObj = $_
        $properties = $oldObj.PSObject.Properties
        foreach ($property in $properties) {
            $columnName = $property.Name
            $oldValue = $property.Value
            if (!$SkipColumnNameRewrite -and ($columnName -match '^(.*)Id$')) {
                Add-Member -InputObject $oldObj -MemberType NoteProperty -Name $columnName -Value "${columnName}_$oldValue" -Force
            }
            if (![string]::IsNullOrEmpty($NullValue) -and $oldValue -eq $NullValue) {
                Add-Member -InputObject $oldObj -MemberType NoteProperty -Name $columnName -Value $null -Force
            }
            if ($ReplaceDateType_1998_01_01__to_1998_01_01T00_00_00 -and ($oldValue -match '([0-9]{4}-[0-9]{2}-[0-9]{2})')) {
                $dateReplacement = "$($oldValue)T00:00:00"
                Add-Member -InputObject $oldObj -MemberType NoteProperty -Name $columnName -Value $dateReplacement -Force
            }
        }
        $oldObj
    }  | Export-Csv -Path (Join-Path $OutputDirectory "$( $FilenamePrefix )$( $csv.Name )") -NoTypeInformation
}

