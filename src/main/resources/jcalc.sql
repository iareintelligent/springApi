-- Create syntax for TABLE 'user'
CREATE TABLE `user` (
                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                        `auth0_user_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `email` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `first_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `last_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `profile_picture_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                        `admin` tinyint(1) DEFAULT '0',
                        `active` tinyint(1) DEFAULT '1',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;