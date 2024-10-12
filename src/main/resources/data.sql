INSERT INTO prize (activity_id, name, probability, stock, is_default, created_at, updated_at) VALUES
(1, 'Prize 1', 0.1, 10, false, NOW(), NOW()),
(1, 'Prize 2', 0.2, 20, false, NOW(), NOW()),
(1, 'Prize 3', 0.3, 30, false, NOW(), NOW()),
(1, 'Default Prize', 0.0, 1000, true, NOW(), NOW());

INSERT INTO draw_chance (user_id, activity_id, total_chances, remaining_chances, created_at, updated_at) VALUES
(1, 1, 5, 5, NOW(), NOW());
