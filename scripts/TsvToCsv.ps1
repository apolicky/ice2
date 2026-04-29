param (
    [string]
    $TsvInputFile,

    [int]
    $NumLines = 10000,

    [int]
    $SkipNumLines = 10000,

    [string]
    $CsvOutputFile,

    
    [string]
    $NullValue = $null,

    [string[]]
    $SkipColumns = @()
)

Get-Content $TsvInputFile | ConvertFrom-Csv -Delimiter "`t" | ForEach-Object {
    if (!($_.titleType -eq "movie")) {
        ;
    }
    else {
        # throw $_
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
    }
} | Export-Csv -Path $CsvOutputFile -Delimiter ',' 
