CREATE TABLE attribute
(
  attributecode integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(50) NOT NULL,
  physicalname varchar(50),
  size integer,
  level integer,
  standard char(1),
  aggregationtype char(1),
  geographic char(1),
  dimensioncode integer NOT NULL,
  attributetype varchar(50),
  CONSTRAINT pk_attribute PRIMARY KEY (attributecode),
  CONSTRAINT fk_attribute_dimension FOREIGN KEY (dimensioncode) REFERENCES dimension (dimensioncode)
)