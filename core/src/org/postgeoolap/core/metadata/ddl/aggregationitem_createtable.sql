CREATE TABLE aggregationitem
(
  aggregationcode integer NOT NULL,
  attributecode integer NOT NULL,
  CONSTRAINT pk_aggregationitem PRIMARY KEY (aggregationcode, attributecode),
  CONSTRAINT fk_aggregationitem_aggregation FOREIGN KEY (aggregationcode) REFERENCES aggregation (aggregationcode),
  CONSTRAINT fk_aggregationitem_attribute FOREIGN KEY (attributecode) REFERENCES attribute (attributecode)
)