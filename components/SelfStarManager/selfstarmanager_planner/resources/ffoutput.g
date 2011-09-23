grammar ffoutput;

start	:	ignore cline+ ignore;
ignore	:	.*;

cline	:	'step'? NUMBER COLON opcall;
opcall 	:	operation (ID)+;
operation
	:	'SETPROPERTY' | 'STARTDEVICE' | 'STOPDEVICE' | 'STARTSERVICE' | 'STOPSERVICE' | 'DEPLOY'
		| 'UNDEPLOY' | 'INITDEVICE' | 'INITCOMPONENT' | 'INITSERVICE' |  'BINDINTERFACE' | 'UNBINDINTERFACE';
		
COLON	:	':';

ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')*
    ;
NUMBER	:	('0'..'9')+;
/*COMMENT
    :   ';;' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    ;
*/
WS  :   ( ' '
        | '\t'
 //       | '\r'
 //       | '\n'
        ) {skip();}
    ;

