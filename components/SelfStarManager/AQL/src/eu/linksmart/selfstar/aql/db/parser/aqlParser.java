/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
/**
 * Copyright (C) 2006-2010 
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */
// $ANTLR 3.2 Sep 23, 2009 12:02:23 aql.g 2010-09-29 00:03:58

package eu.linksmart.selfstar.aql.db.parser;

import eu.linksmart.selfstar.aql.db.QueryTree;

import java.util.Hashtable;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class aqlParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "STRING", "QSTRINGTOKEN", "WS", "'LOCAL'", "'GLOBAL'", "'EQUIJOIN'", "'('", "')'", "'NATURALJOIN'", "'RENAME'", "'['", "'->'", "']'", "'SELECT'", "'=='", "'PROJECT'"
    };
    public static final int EOF=-1;
    public static final int T__9=9;
    public static final int T__8=8;
    public static final int T__7=7;
    public static final int QSTRINGTOKEN=5;
    public static final int T__19=19;
    public static final int T__16=16;
    public static final int WS=6;
    public static final int T__15=15;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int T__10=10;
    public static final int STRING=4;

    // delegates
    // delegators


        public aqlParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public aqlParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return aqlParser.tokenNames; }
    public String getGrammarFileName() { return "aql.g"; }



    // $ANTLR start "start"
    // aql.g:14:1: start returns [QueryTree qt] : q= query EOF ;
    public final QueryTree start() throws RecognitionException {
        QueryTree qt = null;

        QueryTree q = null;


        try {
            // aql.g:15:2: (q= query EOF )
            // aql.g:15:4: q= query EOF
            {
            pushFollow(FOLLOW_query_in_start32);
            q=query();

            state._fsp--;

            match(input,EOF,FOLLOW_EOF_in_start34); 
            qt =q;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return qt;
    }
    // $ANTLR end "start"


    // $ANTLR start "query"
    // aql.g:16:1: query returns [QueryTree qt] : (e= equijoin | n= naturaljoin | s= select | p= projection | t= table | r= rename | l= local | g= global );
    public final QueryTree query() throws RecognitionException {
        QueryTree qt = null;

        QueryTree e = null;

        QueryTree n = null;

        QueryTree s = null;

        QueryTree p = null;

        QueryTree t = null;

        QueryTree r = null;

        QueryTree l = null;

        QueryTree g = null;


        try {
            // aql.g:17:2: (e= equijoin | n= naturaljoin | s= select | p= projection | t= table | r= rename | l= local | g= global )
            int alt1=8;
            switch ( input.LA(1) ) {
            case 9:
                {
                alt1=1;
                }
                break;
            case 12:
                {
                alt1=2;
                }
                break;
            case 17:
                {
                alt1=3;
                }
                break;
            case 19:
                {
                alt1=4;
                }
                break;
            case STRING:
                {
                alt1=5;
                }
                break;
            case 13:
                {
                alt1=6;
                }
                break;
            case 7:
                {
                alt1=7;
                }
                break;
            case 8:
                {
                alt1=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // aql.g:17:4: e= equijoin
                    {
                    pushFollow(FOLLOW_equijoin_in_query50);
                    e=equijoin();

                    state._fsp--;

                    qt =e;

                    }
                    break;
                case 2 :
                    // aql.g:18:6: n= naturaljoin
                    {
                    pushFollow(FOLLOW_naturaljoin_in_query62);
                    n=naturaljoin();

                    state._fsp--;

                    qt =n;

                    }
                    break;
                case 3 :
                    // aql.g:19:5: s= select
                    {
                    pushFollow(FOLLOW_select_in_query72);
                    s=select();

                    state._fsp--;

                    qt =s;

                    }
                    break;
                case 4 :
                    // aql.g:20:5: p= projection
                    {
                    pushFollow(FOLLOW_projection_in_query82);
                    p=projection();

                    state._fsp--;

                    qt =p;

                    }
                    break;
                case 5 :
                    // aql.g:21:5: t= table
                    {
                    pushFollow(FOLLOW_table_in_query93);
                    t=table();

                    state._fsp--;

                    qt =t;

                    }
                    break;
                case 6 :
                    // aql.g:22:4: r= rename
                    {
                    pushFollow(FOLLOW_rename_in_query102);
                    r=rename();

                    state._fsp--;

                    qt =r;

                    }
                    break;
                case 7 :
                    // aql.g:23:4: l= local
                    {
                    pushFollow(FOLLOW_local_in_query111);
                    l=local();

                    state._fsp--;

                    qt =l;

                    }
                    break;
                case 8 :
                    // aql.g:24:4: g= global
                    {
                    pushFollow(FOLLOW_global_in_query120);
                    g=global();

                    state._fsp--;

                    qt =g;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return qt;
    }
    // $ANTLR end "query"


    // $ANTLR start "local"
    // aql.g:25:1: local returns [QueryTree qt] : 'LOCAL' s= source ;
    public final QueryTree local() throws RecognitionException {
        QueryTree qt = null;

        QueryTree s = null;


        try {
            // aql.g:26:2: ( 'LOCAL' s= source )
            // aql.g:26:4: 'LOCAL' s= source
            {
            match(input,7,FOLLOW_7_in_local134); 
            pushFollow(FOLLOW_source_in_local138);
            s=source();

            state._fsp--;

            qt =s; qt.setType(QueryTree.LOCAL);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return qt;
    }
    // $ANTLR end "local"


    // $ANTLR start "global"
    // aql.g:27:1: global returns [QueryTree qt] : 'GLOBAL' s= source ;
    public final QueryTree global() throws RecognitionException {
        QueryTree qt = null;

        QueryTree s = null;


        try {
            // aql.g:28:2: ( 'GLOBAL' s= source )
            // aql.g:28:4: 'GLOBAL' s= source
            {
            match(input,8,FOLLOW_8_in_global152); 
            pushFollow(FOLLOW_source_in_global156);
            s=source();

            state._fsp--;

            qt =s; qt.setType(QueryTree.GLOBAL);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return qt;
    }
    // $ANTLR end "global"


    // $ANTLR start "equijoin"
    // aql.g:29:1: equijoin returns [QueryTree qt] : 'EQUIJOIN' field= qstring '(' inner= query outer= query ')' ;
    public final QueryTree equijoin() throws RecognitionException {
        QueryTree qt = null;

        String field = null;

        QueryTree inner = null;

        QueryTree outer = null;


        try {
            // aql.g:30:2: ( 'EQUIJOIN' field= qstring '(' inner= query outer= query ')' )
            // aql.g:30:4: 'EQUIJOIN' field= qstring '(' inner= query outer= query ')'
            {
            match(input,9,FOLLOW_9_in_equijoin170); 
            pushFollow(FOLLOW_qstring_in_equijoin174);
            field=qstring();

            state._fsp--;

            match(input,10,FOLLOW_10_in_equijoin176); 
            pushFollow(FOLLOW_query_in_equijoin180);
            inner=query();

            state._fsp--;

            pushFollow(FOLLOW_query_in_equijoin184);
            outer=query();

            state._fsp--;

            match(input,11,FOLLOW_11_in_equijoin186); 

            		qt =QueryTree.makeEquiJoinNode(inner,outer,field);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return qt;
    }
    // $ANTLR end "equijoin"


    // $ANTLR start "naturaljoin"
    // aql.g:34:1: naturaljoin returns [QueryTree qt] : 'NATURALJOIN' '(' inner= query outer= query ')' ;
    public final QueryTree naturaljoin() throws RecognitionException {
        QueryTree qt = null;

        QueryTree inner = null;

        QueryTree outer = null;


        try {
            // aql.g:35:2: ( 'NATURALJOIN' '(' inner= query outer= query ')' )
            // aql.g:35:4: 'NATURALJOIN' '(' inner= query outer= query ')'
            {
            match(input,12,FOLLOW_12_in_naturaljoin201); 
            match(input,10,FOLLOW_10_in_naturaljoin203); 
            pushFollow(FOLLOW_query_in_naturaljoin207);
            inner=query();

            state._fsp--;

            pushFollow(FOLLOW_query_in_naturaljoin211);
            outer=query();

            state._fsp--;

            match(input,11,FOLLOW_11_in_naturaljoin213); 

            		qt =QueryTree.makeNaturalJoinNode(inner,outer);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return qt;
    }
    // $ANTLR end "naturaljoin"


    // $ANTLR start "rename"
    // aql.g:40:1: rename returns [QueryTree qt] : 'RENAME' '[' (strsf+= STRING '->' strst+= STRING )+ ']' q= source ;
    public final QueryTree rename() throws RecognitionException {
        QueryTree qt = null;

        Token strsf=null;
        Token strst=null;
        List list_strsf=null;
        List list_strst=null;
        QueryTree q = null;


        try {
            // aql.g:41:2: ( 'RENAME' '[' (strsf+= STRING '->' strst+= STRING )+ ']' q= source )
            // aql.g:41:4: 'RENAME' '[' (strsf+= STRING '->' strst+= STRING )+ ']' q= source
            {
            match(input,13,FOLLOW_13_in_rename229); 
            match(input,14,FOLLOW_14_in_rename231); 
            // aql.g:41:17: (strsf+= STRING '->' strst+= STRING )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==STRING) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // aql.g:41:18: strsf+= STRING '->' strst+= STRING
            	    {
            	    strsf=(Token)match(input,STRING,FOLLOW_STRING_in_rename236); 
            	    if (list_strsf==null) list_strsf=new ArrayList();
            	    list_strsf.add(strsf);

            	    match(input,15,FOLLOW_15_in_rename238); 
            	    strst=(Token)match(input,STRING,FOLLOW_STRING_in_rename242); 
            	    if (list_strst==null) list_strst=new ArrayList();
            	    list_strst.add(strst);


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);

            match(input,16,FOLLOW_16_in_rename246); 
            pushFollow(FOLLOW_source_in_rename250);
            q=source();

            state._fsp--;


            		Hashtable<String,String> map = new Hashtable<String,String>(list_strsf.size());
            		for (int i=0;i<list_strsf.size();i++)
            			map.put(((Token)list_strsf.get(i)).getText(),((Token)list_strst.get(i)).getText());
            		qt =QueryTree.makeRenameNode(q,map);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return qt;
    }
    // $ANTLR end "rename"


    // $ANTLR start "select"
    // aql.g:48:1: select returns [QueryTree qt] : 'SELECT' STRING '==' value= qstring q= source ;
    public final QueryTree select() throws RecognitionException {
        QueryTree qt = null;

        Token STRING1=null;
        String value = null;

        QueryTree q = null;


        try {
            // aql.g:49:2: ( 'SELECT' STRING '==' value= qstring q= source )
            // aql.g:49:4: 'SELECT' STRING '==' value= qstring q= source
            {
            match(input,17,FOLLOW_17_in_select266); 
            STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_select268); 
            match(input,18,FOLLOW_18_in_select270); 
            pushFollow(FOLLOW_qstring_in_select274);
            value=qstring();

            state._fsp--;

            pushFollow(FOLLOW_source_in_select278);
            q=source();

            state._fsp--;


            		qt =QueryTree.makeSelectNode(q,(STRING1!=null?STRING1.getText():null),value);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return qt;
    }
    // $ANTLR end "select"


    // $ANTLR start "projection"
    // aql.g:53:1: projection returns [QueryTree qt] : 'PROJECT' '[' (strs+= STRING )+ ']' q= source ;
    public final QueryTree projection() throws RecognitionException {
        QueryTree qt = null;

        Token strs=null;
        List list_strs=null;
        QueryTree q = null;


        try {
            // aql.g:54:2: ( 'PROJECT' '[' (strs+= STRING )+ ']' q= source )
            // aql.g:54:4: 'PROJECT' '[' (strs+= STRING )+ ']' q= source
            {
            match(input,19,FOLLOW_19_in_projection294); 
            match(input,14,FOLLOW_14_in_projection296); 
            // aql.g:54:18: (strs+= STRING )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==STRING) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // aql.g:54:19: strs+= STRING
            	    {
            	    strs=(Token)match(input,STRING,FOLLOW_STRING_in_projection301); 
            	    if (list_strs==null) list_strs=new ArrayList();
            	    list_strs.add(strs);


            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);

            match(input,16,FOLLOW_16_in_projection305); 
            pushFollow(FOLLOW_source_in_projection309);
            q=source();

            state._fsp--;

            	String[] fields = new String[list_strs.size()];
            		for (int i=0;i<list_strs.size();i++)
            			fields[i]=((Token)list_strs.get(i)).getText();
            		qt =QueryTree.makeProjectNode(fields,q);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return qt;
    }
    // $ANTLR end "projection"


    // $ANTLR start "table"
    // aql.g:60:1: table returns [QueryTree qt] : STRING ;
    public final QueryTree table() throws RecognitionException {
        QueryTree qt = null;

        Token STRING2=null;

        try {
            // aql.g:61:2: ( STRING )
            // aql.g:61:4: STRING
            {
            STRING2=(Token)match(input,STRING,FOLLOW_STRING_in_table325); 
            qt =QueryTree.makeTableNode((STRING2!=null?STRING2.getText():null));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return qt;
    }
    // $ANTLR end "table"


    // $ANTLR start "source"
    // aql.g:62:1: source returns [QueryTree qt] : '(' q= query ')' ;
    public final QueryTree source() throws RecognitionException {
        QueryTree qt = null;

        QueryTree q = null;


        try {
            // aql.g:63:2: ( '(' q= query ')' )
            // aql.g:63:4: '(' q= query ')'
            {
            match(input,10,FOLLOW_10_in_source340); 
            pushFollow(FOLLOW_query_in_source344);
            q=query();

            state._fsp--;

            match(input,11,FOLLOW_11_in_source346); 
            qt =(QueryTree)q;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return qt;
    }
    // $ANTLR end "source"


    // $ANTLR start "qstring"
    // aql.g:64:1: qstring returns [String string] : s= QSTRINGTOKEN ;
    public final String qstring() throws RecognitionException {
        String string = null;

        Token s=null;

        try {
            // aql.g:65:2: (s= QSTRINGTOKEN )
            // aql.g:65:4: s= QSTRINGTOKEN
            {
            s=(Token)match(input,QSTRINGTOKEN,FOLLOW_QSTRINGTOKEN_in_qstring362); 
            string =(s!=null?s.getText():null).substring(1,(s!=null?s.getText():null).length()-1);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return string;
    }
    // $ANTLR end "qstring"

    // Delegated rules


 

    public static final BitSet FOLLOW_query_in_start32 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_start34 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equijoin_in_query50 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_naturaljoin_in_query62 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_select_in_query72 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_projection_in_query82 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_table_in_query93 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rename_in_query102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_local_in_query111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_query120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_7_in_local134 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_source_in_local138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_8_in_global152 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_source_in_global156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_9_in_equijoin170 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_qstring_in_equijoin174 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_equijoin176 = new BitSet(new long[]{0x00000000000A3390L});
    public static final BitSet FOLLOW_query_in_equijoin180 = new BitSet(new long[]{0x00000000000A3390L});
    public static final BitSet FOLLOW_query_in_equijoin184 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_equijoin186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_naturaljoin201 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_naturaljoin203 = new BitSet(new long[]{0x00000000000A3390L});
    public static final BitSet FOLLOW_query_in_naturaljoin207 = new BitSet(new long[]{0x00000000000A3390L});
    public static final BitSet FOLLOW_query_in_naturaljoin211 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_naturaljoin213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_rename229 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_rename231 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_in_rename236 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_rename238 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_in_rename242 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_16_in_rename246 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_source_in_rename250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_select266 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_in_select268 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_select270 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_qstring_in_select274 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_source_in_select278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_projection294 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_projection296 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_in_projection301 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_16_in_projection305 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_source_in_projection309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_table325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_10_in_source340 = new BitSet(new long[]{0x00000000000A3390L});
    public static final BitSet FOLLOW_query_in_source344 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_source346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QSTRINGTOKEN_in_qstring362 = new BitSet(new long[]{0x0000000000000002L});

}