-- POPULATE 'CHAT' TABLE
INSERT INTO chats (id, name, group_name)
VALUES (1, 'First chat', null),
       (2, 'Second chat', null),
       (3, 'Third chat', 'XX-00'),
       (4, 'Fourth chat', null);

-- POPULATE 'USER' TABLE
INSERT INTO users (id, username, first_name)
VALUES (1, 'username1', 'First name'),
       (2, null, 'Second name'),
       (3, 'username3', 'Third name'),
       (4, null, 'Fourth name'),
       (5, null, 'Fifth name'),
       (6, 'username6', 'Sixth name'),
       (7, 'username7', 'Seventh name'),
       (8, 'username8', 'Eighth name'),
       (9, 'username9', 'Ninth name'),
       (10, 'username10', 'Tenth name'),
       (11, 'username11', 'Eleventh name'),
       (12, 'username12', 'Twelfth name');

-- POPULATE 'USERS_CHATS' TABLE
INSERT INTO users_chats (user_id, chat_id)
VALUES (1, 1),
       (2, 1),
       (3, 1),
       (4, 1),
       (5, 1),
       (6, 1),
       (7, 1),
       (2, 2),
       (6, 2),
       (3, 3),
       (5, 3),
       (3, 4);

-- POPULATE 'QUEUE' TABLE
INSERT INTO queues (id, name, size, chat_id)
VALUES (default, 'First name', 20, 1),
       (default, 'Second name', 10, 1),
       (default, 'Third name', 14, 1),
       (default, 'Fourth name', 20, 1),
       (default, 'Fifth name', 20, 1),
       (default, 'Sixth name', 20, 1),
       (default, 'Eighth name', 20, 2),
       (default, 'Seventh name', 20, 3);

-- POPULATE 'PLACES' TABLE
INSERT INTO places (user_id, queue_id, number)
VALUES (1, 1, 1),
       (2, 1, 2),
       (3, 1, 3),
       (4, 1, 4),
       (5, 1, 5),
       (6, 1, 6);

-- POPULATE 'LESSONS' TABLE
INSERT INTO lessons (id, name, full_teacher_name, url, email, phone, chat_id)
VALUES (default, 'First name', 'First teacher name', 'first-url.com', 'first@gmail.com', '+380111111111', 1),
       (default, 'Second name', 'Second teacher name', 'second-url.com', 'second@gmail.com', '+380222222222', 3),
       (default, 'Third name', 'Third teacher name', 'third-url.com', 'third@gmail.com', '+380333333333', 2),
       (default, 'Fourth name', 'Fourth teacher name', 'fourth-url.com', 'fourth@gmail.com', '+380444444444', 1),
       (default, 'Fifth name', 'Fifth teacher name', 'fifth-url.com', 'fifth@gmail.com', '+380555555555', 1),
       (default, 'Sixth name', 'Sixth teacher name', 'sixth-url.com', 'sixth@gmail.com', '+380666666666', 1),
       (default, 'Seventh name', 'Seventh teacher name', 'seventh-url.com', 'seventh@gmail.com', '+380777777777', 1),
       (default, 'Eighth name', 'Eighth teacher name', 'eighth-url.com', 'eighth@gmail.com', '+380888888888', 1),
       (default, 'Ninth name', 'Ninth teacher name', 'ninth-url.com', 'ninth@gmail.com', '+380999999999', 1);

-- POPULATE 'RESPECTS' TABLE
INSERT INTO respects (user_id, chat_id, number_this_month, number_prev_month, total_number)
VALUES (1, 1, 10, 20, 30),
       (2, 1, 20, 30, 50),
       (3, 1, 5, 10, 15),
       (4, 1, -10, 0, -10),
       (5, 1, 34, 4, 7),
       (6, 1, 34, 4, 7),
       (7, 1, 15, 4, 7),
       (8, 1, 14, 4, 7),
       (9, 1, 15, 4, 7),
       (10, 1, 10, 4, 7),
       (11, 1, 24, 4, 7),
       (12, 1, 35, 4, 7),
       (1, 2, 30, 50, 80);