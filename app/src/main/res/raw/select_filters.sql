SELECT f.filter_list_id, 
       COALESCE(l.filter_name, f.filter_name) AS filter_name,
       COALESCE(l.filter_description, f.filter_description) AS filter_description,
       f.enabled, 
       f.version, 
       f.time_last_downloaded, 
       f.time_updated, 
       f.display_order 
  FROM filter_lists f 
  LEFT JOIN filters_localization l
    ON f.filter_list_id = l.filter_list_id
   AND l.language_code = '{0}'
 ORDER BY f.display_order ASC;