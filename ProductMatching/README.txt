The ProductMatching module implements different identity resolution methods 
for products. Please follow the instructions below to run the module:

a. Import the project as a Maven project

--Bag Of Words Model
1.Main Class is in BoWInitializer.java
2.Configure the paths in the beginning of the file as stated in the comments
3.Add/ Remove the experiments configurations from the defineExperiments() method

--Dictionary Model
1.Main Class is in DictionaryInitializer.java
2.Configure the paths in the beginning of the file as stated in the comments
3.Add/ Remove the experiments configurations from the defineExperiments() method
4.*To avoid big computational times use the DictionryInitializerWithThreading.java class

--Enhanced Methods
For the implementation of the enhanced methods use the RapidMinerUtils, UnitConversion, Utils
packages for the necessary file transformations. Please note that the enhanced methods
involve processes realized with other tools such Rapidminer.

RESOURCES
In order to run the module the following files/directories are needed:
1. Product Catalog
2. Gold Standard
3. HTML pages to be matched
4. Properties of the product category (provided in resources)
5. Path to write the experiments results
6. Path to write the log results
In order to run the module for the enhanced approaches the following files/directories are additionally needed:
7. Path to training and test data (provided in resources)
8. Path to csv files with the learned weights as extracted by the Rapidminer process
9. Path to the Units of measurement files (provided in Units_)