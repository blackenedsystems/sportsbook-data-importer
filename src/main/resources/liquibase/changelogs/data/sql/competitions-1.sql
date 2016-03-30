INSERT INTO competition (default_name, category_id, country_code, created, created_by, updated, updated_by)
  SELECT 'Bundesliga', id, 'DE', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system' FROM category WHERE default_name = 'Soccer';
INSERT INTO competition (default_name, category_id, country_code, created, created_by, updated, updated_by)
  SELECT 'Premier League', id, 'GB', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system' FROM category WHERE default_name = 'Soccer';
INSERT INTO competition (default_name, category_id, country_code, created, created_by, updated, updated_by)
  SELECT 'Scottish Championship', id, 'GB', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system' FROM category WHERE default_name = 'Soccer';