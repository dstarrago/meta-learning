# meta-learning
Meta learning framework based on rough set measures

The framework allows batch computing of rough set measures for a number of data sets and to determine a set of rules describing the domain of competence of a classification algorithm. That information can help to choose an appropriate classification algorithm for a certain data set. Included rough set measures are (see <a href="https://github.com/dstarrago/rough-sets">rough-set project</a>):
- rough membership
- rough involvement
- rough agreement
- rough class quality


Usage:

The framework was used to create mappings of the domain of competence of classification algorithms. For that purpose, a significant amount of data sets were processed. The data sets were as diverse as possible, i.e., comming from different application domains, having different dimensions (number of examples and number of descriptive attributes), different attribute types and different apparent complexities; so that the data sets complexity measures covered as much as possible the whole space of data complexity. In that way, the dimensions of the data complexity space were defined by the rough set measures. The C4.5 classification algorithm was mapped and its good behavior region in the complexity space was learned. In that way, we were able to predict the C4.5 behavior for new data sets, just calculating the data set complexity measures and comparing its position in the complexity map with the C4.5 good behavior region. 

For more details see:

- Sanchez Tarrago, D., Bello, R., Herrera, F.: Data Complexity by means of Rough Set Measures. In: Proceedings of the Cuba-Flanders Workshop on Machine Learning and Knowledge Discovery (CF-WML-KD2010). Editorial Feijoó, Central University Marta Abreu de Las Villas (2010). <a href="https://www.researchgate.net/publication/332462004_Data_Complexity_by_means_of_Rough_Set_Measures" target="_blank">(text)</a>

More generally, a number of classification algorithms can be mapped and different volumes can be identified in the space of data complexity as the good behavior domain for each algorithm. Given a new data set, its position in the complexity coordinates can be mapped against the domain of competences of a set of classification algorithms to select the best option.

Developed with:
- Java 1.8
- NetBeans IDE 8.2

Dependencies:
- Weka 3.7
- <a href="https://github.com/dstarrago/rough-sets">RoughSets</a>


