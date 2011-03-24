1. �bungsaufgabe
============

Aufgabe:
----------
Entwickeln Sie in einer objektorientierten Programmiersprache Ihrer Wahl eine Simulation eines programmierbaren Taschenrechners entsprechend folgenden Spezifikationen und l�sen Sie damit die weiter unten beschriebene Testaufgabe.

Eingaben in den Taschenrechner:
--------------------------------------

Eingaben erfolgen in Postfix-Notation (zuerst kommen die Argumente, danach der Operator). Die Anzahl der ben�tigten Argumente h�ngt vom Operator ab. Argumente sind ganze Zahlen oder Ausdr�cke in eckigen Klammern. Zum Beispiel ist `1 2+` eine Anwendung des Operators `+` auf `1` und `2`, die als Ergebnis `3` liefert. Der Ausdruck `1 2 3 4+*-` wird zu `1-(2*(3+4)) = -13` ausgewertet: Der erste Operator addiert die direkt davor stehenden Argumente `3` und `4` zu `7`, der Operator `*` wird auf die nach der Addition direkt davor stehenden Argumente `2` und `7` angewandt, und schlie�lich `-` auf `1` und `14`.

Ausdr�cke in eckigen Klammern werden nicht gleich ausgewertet. Einige Operatoren verarbeiten geklammerte Ausdr�cke als Argumente. Zum Beispiel wird `[2*]` als Argument des Operators `a` selbst als Operator gesehen, der ein Argument mit `2` multipliziert. Der Ausdruck `3[2*]a` wird zu `3 2*` bzw. `6` ausgewertet.

Der Taschenrechner verarbeitet immer ganze Zeilen (abgeschlossen durch einen Zeilenumbruch oder EOF) auf einmal. Nach Verarbeitung jeder Zeile gibt der Taschenrechner-Simulator den aktuellen Zustand (vier Stackelemente, siehe unten) aus.

Architektur:
-------------
Der Taschenrechner besteht aus folgenden Teilen:

* Stack
    Ausdr�cke in Postfix-Notation k�nnen mit Hilfe eines Stacks einfach berechnet werden: Jedes Argument wird als neuer Eintrag am Top-of-Stack abgelegt. Die Ausf�hrung eines Operators nimmt die ben�tigte Anzahl von Argumenten vom Stack und legt Ergebnisse auf den Stack. Die Ausgabe des Taschenrechners soll die obersten vier Stackelemente umfassen.

* Eingabeliste
    Sie enth�lt die noch nicht abgearbeiteten Eingaben (nur ASCII-Zeichen). Alle Eingaben werden zeichenweise in der Reihenfolge in dieser Liste abgearbeitet. Neben direkten Eingaben in den Taschenrechner-Simulator werden auch durch den Operator a Eingaben in diese Liste geschrieben.

Zur Vereinfachung k�nnen f�r den Stack, die Eingabeliste, geklammerte Ausdr�cke und Zahlen (vern�nftig gew�hlte) Maximalgr��en festgelegt werden, bei deren �berschreitung eine Fehlermeldung ausgegeben wird. Nach Fehlermeldungen kann die Programmausf�hrung abgebrochen werden.

Operationen:
---------------
Die Bedeutung folgender Eingaben ist vordefiniert:

* Ziffern `0` bis `9`:
    Alle direkt hintereinander stehenden Ziffern in der Eingabeliste werden zu einer Zahl zusammengesetzt, die als neuer Eintrag auf den Stack gelegt wird.

* `[` und `]`:
    Die �ffnende Klammer bewirkt, dass alle folgenden Eingaben bis zur entsprechenden schlie�enden Klammer als Einheit betrachtet werden, die, beispielsweise in Form eines Strings, als neuer Eintrag auf den Stack gelegt wird. Klammern k�nnen geschachtelt sein.
  
* White space (Leerzeichen, Tabulator, Newline, Return):
    Diese Eingaben sind zur Trennung zweier Zahlen sinnvoll, haben sonst aber keine Bedeutung und werden ignoriert.
  
* Arithmetische Operatoren und Vergleichsoperatoren (`+, -, *, /, %, &, |, =, <, >`):
    Diese zweistelligen Operatoren entsprechen den Grundrechnungen (mit `%` zur Berechnung des Divisionsrestes) und einfachen Vergleichen, wobei `&` bzw. `|` das logische UND bzw. ODER darstellen. Diese Operatoren nehmen die zwei obersten Elemente vom Stack und legen das Ergebnis auf den Stack. Wenn ein Argument keine Zahl ist, soll ein Fehler gemeldet werden, ebenso wenn ein Argument von `&` bzw. `|` kein Wahrheitswert (`0` steht f�r wahr und `1` f�r falsch) ist. Ausgenommen hiervon ist nur `=`: Wenn `=` auf zwei gleiche geklammerte Ausdr�cke oder zwei gleiche Zahlen angewandt wird, soll als Ergebnis `0` (wahr) zur�ckgegeben werden, sonst `1` (falsch). Bei nichtassoziativen Operationen ist auf die Reihenfolge der Argumente zu achten: `4 2-` und `4 2/` und `2 4%` sollen jeweils 2 ergeben, und `4 2>` und `2 4<` sollen wahr liefern. Ein Fehler soll gemeldet werden, wenn das zweite Argument von `/` oder `%` gleich `0` ist.

* Vorzeichen�nderung `~`
    ist nur auf Zahlen definiert.
  
* Kopieren `c`
    ersetzt das oberste Element n am Stack durch eine Kopie des n-ten Elements am Stack. Eine Fehlermeldung wird ausgegeben, wenn n keine positive Zahl ist oder der Stack nicht ausreichend viele Elemente enth�lt.
  
* L�schen `d`
    nimmt das oberste Element n vom Stack und entfernt zus�tzlich das n-ten Element vom Stack. Eine Fehlermeldung wird ausgegeben, wenn n keine positive Zahl ist oder der Stack nicht ausreichend viele Elemente enth�lt.

* Anwenden `a`
    nimmt das oberste Element vom Stack. Ist das Argument ein geklammerter Ausdruck, dann wird dieser (mit den umschlie�enden Klammern durch white space ersetzt) an vorderster Stelle in die Eingabeliste geschrieben, damit die darin enthaltenen Eingaben als n�chste abgearbeitet werden. Ist das Argument eine 	Zahl, dann wird eine Fehlermeldung ausgegeben.

* Ausschalten `q`
    beendet das Programm.

* Konstanten:
    Sie k�nnen beliebigen weiteren Zeichen eine Bedeutung als Konstanten geben. Konstante sind geklammerte Ausdr�cke oder Zahlen. Bei Ausf�hrung eines entsprechenden Zeichens wird die Konstante als oberstes Element auf den Stack gelegt.

Beispiele:
-----------
Einige Beispiele sollen die Verwendung der Operatoren verdeutlichen. Wir beschreiben einen Zustand des Taschenrechners durch den Stackinhalt (links vom Zeichen `#`, Top-of-Stack direkt neben `#`, Eintr�ge durch Leerzeichen getrennt) und die Eingabeliste (rechts von `#`, n�chstes zu verarbeitendes Zeichen direkt neben `#`). Pfeile zwischen solchen Zustandsbeschreibungen zeigen Zustands�nderungen durch Ausf�hrung von Operationen an.

Das erste Beispiel zeigt eine bedingte Anweisung: Auf dem Stack wird `0` (wahr) oder `1` (falsch) erwartet. Abh�ngig davon soll der eine oder andere geklammerte Ausdruck ausgewertet werden. Wir legen zuerst den Ausdruck f�r den wahr-Zweig `[9]` und dann den f�r den falsch-Zweig `[9~]` auf den Stack und f�hren einen Ausdruck `[4c5d2+da]` aus, der die eigentliche bedingte Anweisung darstellt (und als Konstante betrachtet werden kann). Die folgenden Abarbeitungsschritte zeigen, was passiert, wenn am Stack zuvor `0` gelegen ist:

		0 #[9][9~][4c5d2+da]a
	--> 0 [9] #[9~][4c5d2+da]a
	--> 0 [9] [9~] #[4c5d2+da]a
	--> 0 [9] [9~] [4c5d2+da] #a
	--> 0 [9] [9~] #4c5d2+da 
	--> 0 [9] [9~] 4 #c5d2+da 
	--> 0 [9] [9~] 0 #5d2+da 
	--> 0 [9] [9~] 0 5 #d2+da 
	--> [9] [9~] 0 #2+da 
	--> [9] [9~] 0 2 #+da 
	--> [9] [9~] 2 #da 
	--> [9] #a 
	--> # 9  
	--> 9 #


Das n�chste Beispiel zeigt anhand der Berechnung von 3 Faktorielle, wie man rekursive Routinen realisieren kann. Zur Vereinfachung k�rzen wir den Ausdruck `[3c3c1-2c1=[]5cCa3d\*]` durch `A` ab, wobei `C` f�r den Ausdruck `[4c5d2+da]` aus dem vorigen Beispiel steht.

		3 #A3c4d3ca3d
	--> 3 A #3c4d3ca3d
	--> 3 A 3 #c4d3ca3d
	--> 3 A 3 #4d3ca3d
	--> 3 A 3 4 #d3ca3d
	--> A 3 #3ca3d
	--> A 3 3 #ca3d
	--> A 3 A #a3d
	--> A 3 # 3c3c1-2c1=[]5cCa3d* 3d
	--> A 3 3 #c3c1-2c1=[]5cCa3d* 3d
	--> A 3 A #3c1-2c1=[]5cCa3d* 3d
	--> A 3 A 3 #c1-2c1=[]5cCa3d* 3d
	--> A 3 A 3 #1-2c1=[]5cCa3d* 3d
	--> A 3 A 3 1 #-2c1=[]5cCa3d* 3d
	--> A 3 A 2 #2c1=[]5cCa3d* 3d
	--> A 3 A 2 2 #c1=[]5cCa3d* 3d
	--> A 3 A 2 2 #1=[]5cCa3d* 3d
	--> A 3 A 2 2 1 #=[]5cCa3d* 3d
	--> A 3 A 2 1 #[]5cCa3d* 3d
	--> A 3 A 2 1 [] #5cCa3d* 3d
	--> A 3 A 2 1 [] 5 #cCa3d* 3d
	--> A 3 A 2 1 [] A #Ca3d* 3d
	--> A 3 A 2 1 [] A C #a3d* 3d
	...
	--> A 3 A 2 A #a 3d* 3d
	--> A 3 A 2 # 3c3c1-2c1=[]5cCa3d* 3d* 3d
	--> A 3 A 2 3 #c3c1-2c1=[]5cCa3d* 3d* 3d
	--> A 3 A 2 A #3c1-2c1=[]5cCa3d* 3d* 3d
	--> A 3 A 2 A 3 #c1-2c1=[]5cCa3d* 3d* 3d
	--> A 3 A 2 A 2 #1-2c1=[]5cCa3d* 3d* 3d
	--> A 3 A 2 A 2 1 #-2c1=[]5cCa3d* 3d* 3d
	--> A 3 A 2 A 1 #2c1=[]5cCa3d* 3d* 3d
	--> A 3 A 2 A 1 2 #c1=[]5cCa3d* 3d* 3d
	--> A 3 A 2 A 1 1 #1=[]5cCa3d* 3d* 3d
	--> A 3 A 2 A 1 1 1 #=[]5cCa3d* 3d* 3d
	--> A 3 A 2 A 1 0 #[]5cCa3d* 3d* 3d
	--> A 3 A 2 A 1 0 [] #5cCa3d* 3d* 3d
	--> A 3 A 2 A 1 0 [] 5 #cCa3d* 3d* 3d
	--> A 3 A 2 A 1 0 [] A #Ca3d* 3d* 3d
	--> A 3 A 2 A 1 0 [] A C #a3d* 3d* 3d
	...
	--> A 3 A 2 A 1 [] #a 3d* 3d* 3d
	--> A 3 A 2 A 1 # 3d* 3d* 3d
	--> A 3 A 2 A 1 3 #d* 3d* 3d
	--> A 3 A 2 1 #* 3d* 3d
	--> A 3 A 2 # 3d* 3d
	--> A 3 A 2 3 #d* 3d
	--> A 3 2 #* 3d
	--> A 6 # 3d
	--> A 6 3 #d
	--> 6 #

Testaufgabe:
---------------
Diese Testaufgabe ist unbedingt durchzuf�hren: Entwerfen Sie (zum Testen des Taschenrechners) einen m�glichst kurzen und effizienten Ausdruck der entscheidet, ob eine am Stack stehende Zahl eine Primzahl ist oder nicht, und testen Sie mit mehreren, auch gr��eren Zahlen.

Zusatzaufgaben f�r Interessierte:
---------------------------------------
Es stellt sich die Frage, ob man mit diesem Taschenrechner wirklich alles Programmieren kann. Ist es m�glich, damit eine Turing-Maschine zu bauen? Die L�sung dieser Zusatzaufgaben ist nicht verpflichtend und hat keinen Einflu� auf die Beurteilung.
