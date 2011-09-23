grammar aql;


@header {
package eu.hydramiddleware.flamenco.aql.db.parser;

import eu.hydramiddleware.flamenco.aql.db.QueryTree;
import java.util.Hashtable;
}
@lexer::header {
package eu.hydramiddleware.flamenco.aql.db.parser;
}

start returns [QueryTree qt]
	:	q=query EOF {$qt=$q.qt;};
query returns [QueryTree qt]
	:	e=equijoin {$qt=$e.qt;} 
	|   n=naturaljoin {$qt=$n.qt;}
	| 	s=select {$qt=$s.qt;}
	| 	p=projection {$qt=$p.qt;} 
	| 	t=table {$qt=$t.qt;}
	|	r=rename {$qt=$r.qt;}
	|	l=local {$qt=$l.qt;}
	|	g=global {$qt=$g.qt;};
local returns [QueryTree qt]
	:	'LOCAL' s=source {$qt=$s.qt; $qt.setType(QueryTree.LOCAL);};
global returns [QueryTree qt]
	:	'GLOBAL' s=source {$qt=$s.qt; $qt.setType(QueryTree.GLOBAL);};
equijoin returns [QueryTree qt]
	:	'EQUIJOIN' field=qstring '(' inner=query outer=query ')'
	{
		$qt=QueryTree.makeEquiJoinNode($inner.qt,$outer.qt,$field.string);
	};
naturaljoin returns [QueryTree qt]
	:	'NATURALJOIN' '(' inner=query outer=query ')'
	{
		$qt=QueryTree.makeNaturalJoinNode($inner.qt,$outer.qt);
	};

rename returns [QueryTree qt]
	:	'RENAME' '[' (strsf+=STRING '->' strst+=STRING)+ ']' q=source
	{
		Hashtable<String,String> map = new Hashtable<String,String>($strsf.size());
		for (int i=0;i<$strsf.size();i++)
			map.put(((Token)$strsf.get(i)).getText(),((Token)$strst.get(i)).getText());
		$qt=QueryTree.makeRenameNode($q.qt,map);
	}; 
select	returns [QueryTree qt]
	:	'SELECT' STRING '==' value=qstring q=source 
	{
		$qt=QueryTree.makeSelectNode($q.qt,$STRING.text,$value.string);
	};
projection	returns [QueryTree qt]
	:	'PROJECT' '[' (strs+=STRING)+ ']' q=source 
	{	String[] fields = new String[$strs.size()];
		for (int i=0;i<$strs.size();i++)
			fields[i]=((Token)$strs.get(i)).getText();
		$qt=QueryTree.makeProjectNode(fields,$q.qt);
	};
table returns [QueryTree qt]
	:	STRING {$qt=QueryTree.makeTableNode($STRING.text);}; 
source	returns [QueryTree qt]
	:	'(' q=query ')' {$qt=(QueryTree)$q.qt;};
qstring returns [String string]
	:	s=QSTRINGTOKEN {$string=$s.text.substring(1,$s.text.length()-1);};
QSTRINGTOKEN	:	'"' s=(~'"')+ '"';
STRING	:	('a'..'z'|'A'..'Z'|'0'..'9')+;
WS	:	(' '|'\t'|'\r'? '\n')+ {skip();} ;



