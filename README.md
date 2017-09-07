[TOC]

# Introduction #

The Secret Santa Generator is a GUI application that assigns a group of members a randomized secret santa. This application is designed to be used prior to an event where each member has to be assigned a random person to gift to. An event's randomized results can be persisted through a save feature. This is useful because the history is accounted for when the randomized results are generated--a member will not receive a repeated result from a previous event. There are additional features to adjust the random result of a member, which is detailed in the Exclusion or Override sections of this guide.

# Secret Santa Generator #

### Features to add ###

* ~~Update existing table to show full history. When "Generate!" is selected, append new year and list on that table.~~
* ~~After implementing new table, add dropdown selections for each row, under the "Current Year" column, to allow for result overriding.~~
* ~~Add button to add newcomers.~~
* ~~Add button to edit exclusions.~~
* Add button to delete members.
* ~~Convert popup error dialogs to Alert~~
* When opening the program, check if all names in data.csv exist as an entry in exclusions.csv (DataValidator)
* Check if there are duplicate selections when result overriding (DataValidator)
* Consolidate SecretSanta, SecretSantaDisplayType, and SecretSantaDisplayType2 into possibly a single data type. Have a single type that holds both a type for generating and a type for displaying (good idea?)
* After saving, append data.csv instead of creating new file. Refresh table afterwards
* After hitting generate, add some kind of results screen
* Bonus--when hovering over a name, display picture of person. And when hovering over the results column, display 2 pictures on [attendee -> secret santa result]

### Secret Santa Generator - Desktop Program ###

Here's the main screen of the program. The table displays the known history of each member's secret santa of the previous years.

![main.png](https://bitbucket.org/repo/6bMx4M/images/2206990925-main.png)

Using the checkboxes, the user can select who's attending the current year's event. When is a member is unchecked, they are removed from the table. And vise versa--when a member is checked, they are added to the table.

![checkboxes.PNG](https://bitbucket.org/repo/6bMx4M/images/1047779430-checkboxes.PNG)

The button's on the upper-right provide various functionality to be described below.

![buttons.PNG](https://bitbucket.org/repo/6bMx4M/images/81315686-buttons.PNG)

A newcomer can be added. If a duplicate name is added, a warning message will alert the user, and the data will not be updated.

![newcomer.png](https://bitbucket.org/repo/6bMx4M/images/2950672455-newcomer.png)

Exclusions can be edited. This is used for instances when person_X does not know person_Y, therefore, they will not be in their respective secret santa pool names.

Note: When person_X excludes person_Y, person_Y will also be updated to exclude person_X. In additon, if person_X removes person_Y from their exclusion list, then person_Y will also remove person_X from their exclusion list.

![exclusion1.png](https://bitbucket.org/repo/6bMx4M/images/544881425-exclusion1.png)

![exclusion2.png](https://bitbucket.org/repo/6bMx4M/images/274985085-exclusion2.png)

![exclusion3.png](https://bitbucket.org/repo/6bMx4M/images/2597791552-exclusion3.png)

The "Enable Override Mode" enables the user to choose a specific name for a member (aka rig). The rules that determine what names are selectable for person_X's override dropdown are:

* person_X has never received them in previous years
* person_X does not have them in the exclusion list
* Only the names that are currently checked for attending (using the checkboxes) can appear

![override.png](https://bitbucket.org/repo/6bMx4M/images/3286366243-override.png)

Once the user finishes the above steps to...

* Determine attendees
* Add newcomers
* Edit each member's exclusions
* Override results

...The user can now generate the results for this year's secret santa event. The name pool for each member is decided by the rules mentioned in the above step.

Note: Each above step is optional and is not required to simply perform a generate.

![generate.png](https://bitbucket.org/repo/6bMx4M/images/1206611157-generate.png)

All data the program uses is kept under the resources folder. The user should never need to edit them directly.

![files.PNG](https://bitbucket.org/repo/6bMx4M/images/1227191017-files.PNG)

# Setting up my Environment #

### Java JDK ###

* Download and install latest Java SE Development Kit. As of 12/18/2016, latest version found [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

### Eclipse ###

* Download and install latest Eclipse IDE. As of 12/18/2016, latest version found [here](https://eclipse.org/downloads/).
* To keep our formatting consistent so we can easily see diffs, import formatter_profile.xml found in the [Downloads](https://bitbucket.org/evant179/secretsantagenerator/downloads). Select ET_Profile as the profile. Here's where you can import the settings:

![code_formatter.png](https://bitbucket.org/repo/6bMx4M/images/326307154-code_formatter.png)

### JavaFX ###

* Set up JavaFX for Eclipse. Instructions found [here](https://www.eclipse.org/efxclipse/install.html).

### Running the program ###

* Within Eclipse, here's a screenshot of the Run Configuration. Run it as a Java Application.

![eclipse_run_config.png](https://bitbucket.org/repo/6bMx4M/images/935062552-eclipse_run_config.png)
