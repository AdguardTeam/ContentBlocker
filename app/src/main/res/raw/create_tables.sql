CREATE TABLE filter_lists (
filter_list_id INTEGER,
filter_name VARCHAR(255),
filter_description VARCHAR(1024),
enabled INTEGER,
version VARCHAR(255),
time_last_downloaded BIGINT, 
time_updated BIGINT,
display_order INTEGER
);

CREATE TABLE filters_localization (
filter_list_id INTEGER,
language_code VARCHAR(2),
filter_name VARCHAR(255),
filter_description VARCHAR(1024)
);

CREATE TABLE traffic_stats (
  package_name VARCHAR(100),
  report_date INTEGER,
  bytes_received INTEGER,
  bytes_sent INTEGER,
  bandwidth_saved INTEGER,
  blocked_ads INTEGER,
  blocked_threats INTEGER
);