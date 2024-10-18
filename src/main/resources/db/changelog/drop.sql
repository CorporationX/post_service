DROP TABLE IF EXISTS post_album;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS post_ad;
DROP TABLE IF EXISTS post CASCADE ;
DROP TABLE IF EXISTS favorite_albums;
DROP INDEX IF EXISTS album_author_title_idx;
DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS resource_id;
DROP TABLE IF EXISTS resource;

DELETE FROM databasechangelog where filename = 'post_V005__alter_post.sql';