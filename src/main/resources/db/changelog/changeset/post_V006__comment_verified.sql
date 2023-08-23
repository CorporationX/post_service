ALTER TABLE comment
   ADD verified boolean DEFAULT false NOT NULL,
   ADD verified_at timestamptz;
