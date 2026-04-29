# Input Datasets

## job_salary_prediction_dataset

- version 1, downloaded 4.4.2026
- [link to dataset](https://www.kaggle.com/datasets/nalisha/job-salary-prediction-dataset/data)
-

## formula 1

- [link to dataset](https://www.kaggle.com/datasets/rohanrao/formula-1-world-championship-1950-2020)
- version 24, 4.4.2026
- processed versions

### f2

- run from `ic`:
  `.\scripts\ProcessDataset.ps1 -CsvDirectory .\inputs\f1\ -OutputDirectory .\inputs\f2\ -FilenamePrefix 'f2-' -NullValue '\N' -SkipColumnNameRewrite -ReplaceDateType_1998_01_01__to_1998_01_01T00_00_00`
- does the following:
  - prefix the names with `f2-` and place it to inputs
  - replace all `\N` by an empty string; make it `null` respectively
  - run this to replace all dates with better format
  - from: `([0-9]{4}-[0-9]{2}-[0-9]{2})` (match as regex)
  - replacement: `$1T00:00:00`
  - keeps the `xyzId` columns with the same value, no rewrites to use different domain

### f2-2

- same as [f2](#f2)
- doesn't run on following files:
  - f2-lap_times.csv
  - f2-pit_stops.csv
  - f2-qualifying.csv

### f3

- f3: run from `ic`:
  `.\scripts\ProcessDataset.ps1 -CsvDirectory .\inputs\f1\ -OutputDirectory .\inputs\f2\ -FilenamePrefix 'f2-' -NullValue '\N' -SkipColumnNameRewrite -ReplaceDateType_1998_01_01__to_1998_01_01T00_00_00`
- does the following:
  - prefix the names with `f3-` and place it to inputs
  - replace all `\N` by an empty string; make it `null` respectively
  - run this to replace all dates with better format; same as previous
  - replaces the `xyzId` columns with numbers to have the prefix of column name

## Yelp

- <https://www.kaggle.com/datasets/yelp-dataset/yelp-dataset>, 6.4.2026

### yelp1

- configure app.properties to use this type of timestamp `yyyy-MM-dd HH:mm:ss`
- need to remove some weird symbols from the dataset bc IND search doesn't like it
- `.\scripts\JsonToCsv.ps1 .\inputs\yelp\yelp_academic_dataset_user.json -NumLines 50000 -CsvOutputFile .\inputs\yelp\yelp1-user_50k.csv -SkipColumns "friends"`; also 500 and 5k
- for user dataset skipping column "friends" because it adds too much complexity
- \+ tinker dataset so that the references are within domain of user/business for tip dataset: `.\scripts\TinkerYelpTipData.ps1 -CsvInputFile__Tip .\inputs\yelp\yelp1-tip_5k.csv -CsvInputFile__Business .\inputs\yelp\yelp1-business_5k.csv -CsvInputFile__User .\inputs\yelp\yelp1-user_5k.csv -CsvOutputFile__Tip .\inputs\yelp\yelp1-tip_5k-tinkered.csv`

## IMDB

- <https://datasets.imdbws.com/>, 8.4.2026

- title.tsv `.\scripts\TsvToCsv.ps1 -TsvInputFile .\inputs\imdb\title.basics.tsv -CsvOutputFile .\inputs\imdb\title.basics.csv -NullValue "\N"`
  - all movies from the dataset ☝️
  - 20k `.\scripts\ShortenCsvFile.ps1 -CsvInputFile .\inputs\imdb\title.basics.csv -CsvOutputFile .\inputs\imdb\title.basics_20k.csv -NumLines 20000 -SkipEachXthLine 10`
  - 200k `.\scripts\ShortenCsvFile.ps1 -CsvInputFile .\inputs\imdb\title.basics.csv -CsvOutputFile .\inputs\imdb\title.basics_200k.csv -NumLines 200000 -SkipEachXthLine 10`
- ratings.tsv `.\scripts\TinkerImdbData.ps1 -CsvInputFile__ImdbTitle .\inputs\imdb\title.basics.csv -TsvInputFile .\inputs\imdb\title.ratings.tsv -CsvOutputFile .\inputs\imdb\title.ratings.csv -OnlyKnownMovies`
  - all ratings from the dataset ☝️
  - 20k `.\scripts\TinkerImdbData.ps1 -CsvInputFile__ImdbTitle .\inputs\imdb\title.basics_20k.csv -TsvInputFile .\inputs\imdb\title.ratings.tsv -CsvOutputFile .\inputs\imdb\title.ratings_20k.csv -OnlyKnownMovies`
  - 200k `.\scripts\TinkerImdbData.ps1 -CsvInputFile__ImdbTitle .\inputs\imdb\title.basics_200k.csv -TsvInputFile .\inputs\imdb\title.ratings.tsv -CsvOutputFile .\inputs\imdb\title.ratings_200k.csv -OnlyKnownMovies`
- title.akas
  - stop the script once you've seen X amount of movie ids
  - 20k `.\scripts\TinkerImdbData.ps1 -CsvInputFile__ImdbTitle .\inputs\imdb\title.basics_20k.csv -TsvInputFile .\inputs\imdb\title.akas.tsv -CsvOutputFile .\inputs\imdb\title.akas_20k.csv -OnlyKnownMovies -TitleIdHeader titleId -NullValue "\N" -CountSeenMovies`
  - 200k `.\scripts\TinkerImdbData.ps1 -CsvInputFile__ImdbTitle .\inputs\imdb\title.basics_200k.csv -TsvInputFile .\inputs\imdb\title.akas.tsv -CsvOutputFile .\inputs\imdb\title.akas_200k.csv -OnlyKnownMovies -TitleIdHeader titleId -NullValue "\N" -CountSeenMovies`
    - stopped after reading 45M lines read, seen 199533 movies instead of 200k.
- name.tsv `.\scripts\TinkerImdbData.ps1 -CsvInputFile__ImdbTitle .\inputs\imdb\title.basics.csv -TsvInputFile .\inputs\imdb\title.ratings.tsv -CsvOutputFile .\inputs\imdb\title.ratings.csv -OnlyKnownMovies`
