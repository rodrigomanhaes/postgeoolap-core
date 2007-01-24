CREATE VIEW cnsaggregation AS 
  SELECT aggregation.aggregationcode, aggregation.name AS aggregationname, 
    aggregation.base, aggregation.sqlbase, aggregation.sorting, 
    aggregation.cubecode, attribute.attributecode, attribute.name AS attributename,
    attribute.attributetype AS attributetype, 
    attribute.physicalname AS attributephysicalname, attribute.level, attribute.standard, 
 	attribute.aggregationtype, attribute.geographic, dimension.dimensioncode,
	dimension.name AS tablename, dimension.dimensionname as dimensionname, 
	imension.dimensiontype, dimension.clause, dimension.tablecode
   FROM dimension dimension
   JOIN (attribute attribute
       JOIN (aggregation aggregation
           JOIN aggregationitem aggregationitem 
           ON aggregation.aggregationcode = aggregationitem.aggregationcode) 
       ON attribute.attributecode = aggregationitem.attributecode) 
   ON dimension.dimensioncode = attribute.dimensioncode