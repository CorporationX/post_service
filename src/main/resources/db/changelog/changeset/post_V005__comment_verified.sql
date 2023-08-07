ALTER TABLE comment (
   ADD verified boolean DEFAULT false NOT NULL,
   ADD verifiedDate timestamptz
);