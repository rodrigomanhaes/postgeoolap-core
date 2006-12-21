CREATE TABLE aggregation
(
  aggregationcode integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(50) NOT NULL,
  base varchar(1),
  sqlbase varchar(500),
  cubecode integer NOT NULL,
  sorting integer,
  CONSTRAINT pk_aggregation PRIMARY KEY (aggregationcode),
  CONSTRAINT fk_aggregation_cube FOREIGN KEY (cubecode) REFERENCES cube (cubecode)
)