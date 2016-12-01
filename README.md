# Secret Santa Generator#



### Features to add ###

* ~~Update existing table to show full history. When "Generate!" is selected, append new year and list on that table.~~
* ~~After implementing new table, add dropdown selections for each row, under the "Current Year" column, to allow for result overriding.~~
* ~~Add button to add newcomers.~~
* ~~Add button to edit exclusions.~~
* Add button to delete members.
* Convert popup error dialogs to Alert
* When opening the program, check if all names in data.csv exist as an entry in exclusions.csv (DataValidator)
* Check if there are duplicate selections when result overriding (DataValidator)
* Consolidate SecretSanta, SecretSantaDisplayType, and SecretSantaDisplayType2 into possibly a single data type
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