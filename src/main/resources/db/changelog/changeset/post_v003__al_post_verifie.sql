--changeset pavlick2745:1
ALTER TABLE post
    ADD COLUMN is_verified BOOLEAN default false not null;

ALTER TABLE post
    ADD COLUMN verified_date TIMESTAMP;

INSERT INTO post(content, verified_date)
VALUES ('asdsadsa asd', null),
       ('asdsadsa aa swearing1 aaasd', null),
       ('asdsadsa aaswearing212 ', null),
       ('asdsadsa asd', null),
       ('content2', '2024-08-02'),
       ('content3', '2024-08-02');