SELECT f.filter_list_id,
       COALESCE(lfull.filter_name, lsimple.filter_name, f.filter_name) AS filter_name,
       COALESCE(lfull.filter_description, lsimple.filter_description, f.filter_description) AS filter_description,
       f.enabled,
       f.version,
       f.time_last_downloaded,
       f.time_updated,
       f.display_order
  FROM filter_lists f
  LEFT JOIN filters_localization lfull
    ON f.filter_list_id = lfull.filter_list_id
   AND lfull.language_code = '{0}'
  LEFT JOIN filters_localization lsimple
    ON f.filter_list_id = lsimple.filter_list_id
   AND lsimple.language_code = '{1}'
 ORDER BY f.display_order ASC, f.filter_name ASC;