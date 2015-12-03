# Hafslund #

The Norwegian energy company [Hafslund](http://www.hafslund.no) is using Duke to identify duplicate contacts in their ERP system. Duke runs in a web application which receives a stream of updates from the ERP and continuously updates and maintains a database of duplicate links, which are fed back into a search engine.

The original development of Duke (roughly up to version 0.2) was financed by Hafslund, which graciously allowed me to open source the software.

# Yoxel Portal #

[Yoxel Portal](http://yoxel.com) is a CRM cloud service for keeping customer contacts synchronized across different systems. It uses Duke to deduplicate contact records, as described [here](http://yoxel.uservoice.com/knowledgebase/articles/147010-how-does-yoxel-treat-contacts-with-the-same-name-i).

# Cityhotels #

[Cityhotels](http://cityhotels.no) is a hotel booking portal which collects hotel information and prices from multiple sources. It needs to deduplicate the hotels to aggregate the prices from the different sources to a single hotel instance, and uses Duke for this.

# Easyrec #

[The Easyrec](http://easyrec.sourceforge.net/wiki/index.php/Main_Page) recommendation engine uses Duke to create links between similar items being recommended, in the [Profile Similarity Calculator](http://easyrec.sourceforge.net/wiki/index.php/Profile_Similarity_Calculator).

# Others #

If you want to be listed here, please send an email, and I'll add you.