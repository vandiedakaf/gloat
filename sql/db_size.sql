SELECT
  s.schema_name,
  sp.grantee                                                                             user,
  cast(round(sum(coalesce(t.data_length + t.index_length, 0)) / 1024 / 1024, 3) AS CHAR) db_size_mb,
  sp.has_insert
FROM
  information_schema.schemata s
  INNER JOIN
  information_schema.tables t ON s.schema_name = t.table_schema
  INNER JOIN (
               SELECT
                 spi.grantee,
                 spi.table_schema,
                 max(
                     CASE
                     WHEN spi.privilege_type = 'INSERT'
                       THEN 1
                     ELSE 0
                     END
                 ) has_insert
               FROM
                 information_schema.schema_privileges spi
               GROUP BY
                 spi.grantee
                 , spi.table_schema
             ) sp ON s.schema_name = sp.table_schema
GROUP BY
  s.schema_name;