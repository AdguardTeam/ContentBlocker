INSERT INTO filter_lists (filter_list_id, filter_name, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (11,'Mobile ads filter',0,'1.0.0.2',1413915570477,1413915570477,99);
INSERT INTO filters_localization (filter_list_id, language_code, filter_name) VALUES (11,'ru','Фильтр мобильной рекламы');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name) VALUES (11,'en','Mobile ads filter');
UPDATE filter_lists
   SET enabled = 1
 WHERE filter_list_id = 11;