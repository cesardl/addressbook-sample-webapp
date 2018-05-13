Address Book [![Build Status](https://travis-ci.org/cesardl/addressbook-sample-webapp.svg?branch=master)](https://travis-ci.org/cesardl/addressbook-sample-webapp) [![Maintainability](https://api.codeclimate.com/v1/badges/ad8b96409b766ee88044/maintainability)](https://codeclimate.com/github/cesardl/addressbook-sample-webapp/maintainability)
===========

AddressBook es un proyecto de ejemplo que realic&eacute; all&aacute; por el a&ntilde;o 2010. Utiliza JSF para la parte visual y consta de dos implementaciones

1. Aplicaci&oacute;n con Richfaces
2. Aplicaci&oacute;n con Icefaces

Inicialmente eran proyectos NetBeans basado en Java 6, los he refactorizado y ahora son multim&oacute;dulos basados en Maven.

Contiene b&aacute;sicamente un CRUD de contactos relacionado al usuario logueado. Soporta el almacenamiento de im&aacute;genes en la base de datos y la generaci&oacute;n de reportes PDF con JasperReports.

Considerar el siguiente comando con el que se hizo un backup de la base de datos.

`
mysqldump -u <user> -p address_book > address_book_schema.sql
`

Me aprovech&eacute; de una base de datos de ejemplo llamada **_Employees_** en donde insert&eacute; cerca de 30k registros como contactos. Ahora la aplicaci&oacute;n tiene problemas  de performance que deben ser corregidos.

Esta es la query con la que obtuve los datos desde el schema _Employees_.

`
insert into address_book.contacto(con_nombres, con_cumpleanos, usu_id)
select concat(first_name, ' ', last_name), hire_date, 1 from employees;
`
