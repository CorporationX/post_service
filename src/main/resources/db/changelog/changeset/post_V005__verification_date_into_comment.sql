ALTER TABLE comment
ADD verified BOOLEAN ;

ALTER TABLE comment
ADD verification_date timestamptz;