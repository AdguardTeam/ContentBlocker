DROP TABLE IF EXISTS traffic_stats;

create table traffic_stats (
  package_name VARCHAR(100),
  report_date INTEGER,
  bytes_received INTEGER,
  bytes_sent INTEGER,
  bandwidth_saved INTEGER,
  blocked_ads INTEGER,
  blocked_threats INTEGER);