[CmdletBinding()]
param (
    [ValidateScript({ Test-Path $_ })]
    [string]
    $CsvInputFile__Tip,

    [ValidateScript({ Test-Path $_ })]
    [string]
    $CsvInputFile__Business,

    [ValidateScript({ Test-Path $_ })]
    [string]
    $CsvInputFile__User,

    [Parameter(Mandatory = $true)]
    [string]
    $CsvOutputFile__Tip
)


[string[]] $businessIds = Get-Content -Path $CsvInputFile__Business | ConvertFrom-Csv | ForEach-Object {
    $_.business_id
}
$setOfBusinessIds = [System.Collections.Generic.HashSet[string]]::new($businessIds)
[int] $numLinesBusiness = (Get-Content -Path $CsvInputFile__Business | Measure-Object -Property Length).Count
# throw "Number of business ids: $numLinesBusiness"
[string[]] $userIds = Get-Content -Path $CsvInputFile__User | ConvertFrom-Csv | ForEach-Object {
    $_.user_id

}
[int] $numLinesUser = (Get-Content -Path $CsvInputFile__User | Measure-Object -Property Length).Count
$setOfUserIds = [System.Collections.Generic.HashSet[string]]::new($userIds)


[int] $counter = 0

Import-Csv -Path $CsvInputFile__Tip -Delimiter ","  | ForEach-Object {
    if (!$setOfBusinessIds.Contains($_.business_id)) {
        # $randomBusinessId = Get-Random -InputObject $setOfBusinessIds
        $_.business_id = $businessIds[$counter++ % $numLinesBusiness]
    }
    if (!$setOfUserIds.Contains($_.user_id)) {
        # $randomUserId = Get-Random -InputObject $setOfUserIds
        $_.user_id = $userIds[$counter++ % $numLinesUser]
    }
    $_.text = $_.text -replace "`r`n", " " -replace "`n", " " -replace "\^", " " -replace "\\", " " # something really weird in one of the comments. IND search doesn't like it
    $_
} | Export-Csv -Path $CsvOutputFile__Tip -Delimiter ',' 