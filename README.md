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
Die Activity Time wird aufgerufen sobald eine Zeitbedingung aufgerufen oder erstellt wird. Hierbei ist es möglich einen Zeitraum oder Zeitpunkt auszuwählen, sowie die Wochentage an denen diese Bedingung aktiviert werden soll. Für die Anzeige der Wochentage wurden mehrere Toggle-Buttons erstellt, die in einem Flowlayout (keine Eigenentwicklung) angeordnet werden.

##### Location-Condition
Bereits beim Start der App wird ein Service gestartet, der für den Umgang mit Geofences verantwortlich ist. Hier werden die alle Geofences registriert, aktualisiert und gelöscht. Wenn ein Geofence betreten wird, wird das Ereignis an den Service übergeben und die zutreffende Regel wird ausgeführt.

##### Map-Page
Wenn ein neuer Geofence hinzugefügt werden soll, wird dazu Google Maps geöffnet. Mit einem langen Klick auf die Map wird ein neuer Geofence angelegt. So lange der Geofence ausgewählt ist kann dieser bearbeitet werden. Der Radius kann mit einem Slider verändert werden. Den Marker kann man mit Hilfe eines langen Klicks auf den Marker auf der Map verschieben. Um den aktiven Marker abzuwählen wird einfach auf die Karte geklickt. 

##### Location Import
Falls der User nich seine eigene Locations erstellen möchte, hat er die Möglichkeit von einem Zentralen Server, Locations von anderen Nutzern zu Importieren. Dafür öffnet er die Import Seite, wo alle Location-Bedingungen des Servers in einer Liste angezeigt werden. Diese Liste ist zudem nach dem Namen filterbar (Adapter und Filter in ImportFilter). Wählt er eine Location aus und bestätigt den Import, werden die Daten von dem Server geladen und in die lokale Datenbank übertragen.

#### Rule-Action
Der dritte Reiter der Regel ist das Action Fragment. Dieses enthält ein Expandable Listview mit allen Aktionen, die bei dem Eintreten der zuvor erstellten Bedingungen ausgelöst werden sollen. Dazu wurde ein ExpandableListViewAdapter erstellt, der das Verhalten der Liste steuert. Zunächst wird für jede mögliche Aktion eine Gruppe (Group.java) erstellt. Diese Gruppen (Message, Notification, Sound, WLAN, Bluetooth) enthalten jeweils Child-Objekte die die einzelnen Konfigurationsmöglichkeiten der Gruppe enthalten (Beispielsweise enthält Message ein Kind für die Nummer des Empfängers und ein Kind für die Nachricht selbst). 
Sobald die Gruppen und Kinder erstellt wurden, wird die Liste im UI aufgebaut. Dabei enthält jede Gruppe eine Header Zeile mit Namen und Aktiv-Zustand (listrow_group.xml). Diese Header Rows werden bei einem Klick auf den Namen aufgeklappt, wonach die Kinder angezeigt werden. Diese sind je nach Art beispielsweise ein Textinput (row_textinput.xml) oder ein Switch (row_switch.xml).
Sobald der User in diesen Kindern einen Input vornimmt, werden die Daten in die jeweiligen Group/Child Objekte geschrieben und die Group wird in die lokale Datenbank übertragen.

## Datenbank
In einer mit der App verknüpften SQLite-Datenbank werden sämtliche Regeln, Aktionen und Bedingungen persistent gespeichert.
Die Datenbank hat dabei den im folgenden ER-Diagramm dargestellten Aufbau:
![Alt text](/documentation/client-erd.png?raw=true "ER-Diagramm SQLite-Datenbank")
Das Package "database" enthält dabei alle Java-Klassen, die zum Laden und Speichern der Objekte in der Datenbank benötigt werden. Die Objekte dienen zum Einen als Schnittstelle für die CRUD-Operationen, zum Anderen verwalten sie aber auch die Objekte während der Laufzeit in der App.
Das dazugehörige Klassendiagramm verdeutlicht den Aufbau der Klassen:
![Alt text](/documentation/class-diagram-database.png?raw=true "Klassendiagram des database-Packages")

# Google docs
https://docs.google.com/document/d/17CKXoovYEVOGd0I7jC6lP1RXxDGdKBsRTpvCP-Szjw0/edit#heading=h.ajzkjcangzco

#Mockups
https://www.fluidui.com/editor/live/preview/p_nCXqi4gkSKM7vOWaqdReJT5UI3fNi1eU.1433147650261


# Use Google Maps
Change Google keystore to use Google Maps.

- Open directory User/.android 
- replace debug.keystore with github debug.keystore file
