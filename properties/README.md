The files in this directory are templates for the configuration of the various data integration service.  

The placeholders in each file should be replaced with values that are appropriate for the environment in which the data 
importer is executed.

Where these files are stored is up to you, but they should not be stored in this project.  At runtime the application 
expects to find appropriate property files in the directory indicated by the property: external.properties.location 
(see src/main/resources/application.properties).                                              