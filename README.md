# MobileAndroid
Native mobile Android project 6th semester

#GEO
Der Generische Event Organizer (GEO) soll es ermöglichen auf Basis von Locations (Geofences) und Zeiträumen verschiedene Events auf einem Handy zu erstellen.
Beispielsweise ist es möglich ein Event so anzulegen, das während der Arbeit (Arbeitsort und Arbeitszeit) der Ton das Handys auf leise gestellt wird, sodass Anrufe nicht bei der Arbeit stören. Auch eine automatische Ein-/Ausschaltung des WLAN-Adapters des Handys ist möglich, sodass diese Funktion immer dann agestellt wird, wenn der Nutzer zu Hause ist.

##Benutzeroberfläche
Die Benutzeroberfläche ist im Ordner "UI" des Android Projektes enthalten.
###Main-Page
Die Hauptseite enthält eine Übersicht über alle Regeln die ein Nutzer angelegt hat. Diese werden in einem Listview, welches mit einem speziellen ArrayAdapter ("DBRuleAdapter") erstellt wird. Zusätzlich zu dem Namen der Regel wird noch ein Icon angezeigt, welches symbolisiert ob die Regel aktiv ist oder nicht.

###Rule-Page
Sobald eine Regel ausgewählt ist oder eine neue Regel erstellt wird, wird die Regel-Seite aufgerufen.
Diese Seite ist ein Konstrukt aus 3 unterschiedlichen Tabs, die mit der Tableiste oder per "swipen" der Seite aufrufbar sind. Die Haupt-Activity, die die 3 Fragments für die Tabs enthält ist die RuleContainer-Activity.

#### Rule-General
Der erste Tab der Rule-Seite ist das RuleGeneral Fragment, es ermöglicht das Löschen, das Ändern des Namens und das Setzen des Aktiv-Zustandes der Regel.

#### Rule-Condition
Das RuleCondition Fragment ist der zweite Teil der Rule-Activity. Sie enthält eine Liste mit allen Bedingungen, die in der Regel enthalten sind. Wiederum wird hierbei ein ListView mit einem speziellen Adapter ("ConditionAdapter") verwendet, der den Namen sowie ein Icon für den Typ (Location oder Zeit) der Bedingung anzeigt.

##### Time-Condition

##### Location-Condition

##### Location Import

#### Rule-Action

# Google docs
https://docs.google.com/document/d/17CKXoovYEVOGd0I7jC6lP1RXxDGdKBsRTpvCP-Szjw0/edit#heading=h.ajzkjcangzco

#Mockups
https://www.fluidui.com/editor/live/preview/p_nCXqi4gkSKM7vOWaqdReJT5UI3fNi1eU.1433147650261

# Usage of DBObjects
## Example Usage of DBRule;
Create a new rule:
```
DBRule rule = new DBRule(); // Create object
rule.setName("Notification Rule"); // set attributes
rule.setActive(true);
rule.writeToDB(); // write to database
´´´
load all rules:
```

´´´

# Use Google Maps
Change Google keystore to use Google Maps.

- Open directory User/.android 
- replace debug.keystore with github debug.keystore file
