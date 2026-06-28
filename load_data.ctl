OPTIONS (SKIP=1)
LOAD DATA
INFILE 'dataset.csv'
APPEND
INTO TABLE milling_data
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
  udi,
  product_id,
  process_temp,
  torque,
  machine_failure
)