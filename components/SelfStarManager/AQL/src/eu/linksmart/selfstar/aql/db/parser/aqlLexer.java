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


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class aqlLexer extends Lexer {
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

    public aqlLexer() {;} 
    public aqlLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public aqlLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "aql.g"; }

    // $ANTLR start "T__7"
    public final void mT__7() throws RecognitionException {
        try {
            int _type = T__7;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:7:6: ( 'LOCAL' )
            // aql.g:7:8: 'LOCAL'
            {
            match("LOCAL"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__7"

    // $ANTLR start "T__8"
    public final void mT__8() throws RecognitionException {
        try {
            int _type = T__8;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:8:6: ( 'GLOBAL' )
            // aql.g:8:8: 'GLOBAL'
            {
            match("GLOBAL"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__8"

    // $ANTLR start "T__9"
    public final void mT__9() throws RecognitionException {
        try {
            int _type = T__9;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:9:6: ( 'EQUIJOIN' )
            // aql.g:9:8: 'EQUIJOIN'
            {
            match("EQUIJOIN"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__9"

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:10:7: ( '(' )
            // aql.g:10:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__10"

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:11:7: ( ')' )
            // aql.g:11:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:12:7: ( 'NATURALJOIN' )
            // aql.g:12:9: 'NATURALJOIN'
            {
            match("NATURALJOIN"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:13:7: ( 'RENAME' )
            // aql.g:13:9: 'RENAME'
            {
            match("RENAME"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:14:7: ( '[' )
            // aql.g:14:9: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:15:7: ( '->' )
            // aql.g:15:9: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:16:7: ( ']' )
            // aql.g:16:9: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:17:7: ( 'SELECT' )
            // aql.g:17:9: 'SELECT'
            {
            match("SELECT"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:18:7: ( '==' )
            // aql.g:18:9: '=='
            {
            match("=="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:19:7: ( 'PROJECT' )
            // aql.g:19:9: 'PROJECT'
            {
            match("PROJECT"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "QSTRINGTOKEN"
    public final void mQSTRINGTOKEN() throws RecognitionException {
        try {
            int _type = QSTRINGTOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            int s;

            // aql.g:66:14: ( '\"' (s= (~ '\"' ) )+ '\"' )
            // aql.g:66:16: '\"' (s= (~ '\"' ) )+ '\"'
            {
            match('\"'); 
            // aql.g:66:21: (s= (~ '\"' ) )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='\u0000' && LA1_0<='!')||(LA1_0>='#' && LA1_0<='\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // aql.g:66:21: s= (~ '\"' )
            	    {
            	    // aql.g:66:22: (~ '\"' )
            	    // aql.g:66:23: ~ '\"'
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QSTRINGTOKEN"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:67:8: ( ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )+ )
            // aql.g:67:10: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )+
            {
            // aql.g:67:10: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')||(LA2_0>='A' && LA2_0<='Z')||(LA2_0>='a' && LA2_0<='z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // aql.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // aql.g:68:4: ( ( ' ' | '\\t' | ( '\\r' )? '\\n' )+ )
            // aql.g:68:6: ( ' ' | '\\t' | ( '\\r' )? '\\n' )+
            {
            // aql.g:68:6: ( ' ' | '\\t' | ( '\\r' )? '\\n' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=4;
                switch ( input.LA(1) ) {
                case ' ':
                    {
                    alt4=1;
                    }
                    break;
                case '\t':
                    {
                    alt4=2;
                    }
                    break;
                case '\n':
                case '\r':
                    {
                    alt4=3;
                    }
                    break;

                }

                switch (alt4) {
            	case 1 :
            	    // aql.g:68:7: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;
            	case 2 :
            	    // aql.g:68:11: '\\t'
            	    {
            	    match('\t'); 

            	    }
            	    break;
            	case 3 :
            	    // aql.g:68:16: ( '\\r' )? '\\n'
            	    {
            	    // aql.g:68:16: ( '\\r' )?
            	    int alt3=2;
            	    int LA3_0 = input.LA(1);

            	    if ( (LA3_0=='\r') ) {
            	        alt3=1;
            	    }
            	    switch (alt3) {
            	        case 1 :
            	            // aql.g:68:16: '\\r'
            	            {
            	            match('\r'); 

            	            }
            	            break;

            	    }

            	    match('\n'); 

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);

            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // aql.g:1:8: ( T__7 | T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | QSTRINGTOKEN | STRING | WS )
        int alt5=16;
        alt5 = dfa5.predict(input);
        switch (alt5) {
            case 1 :
                // aql.g:1:10: T__7
                {
                mT__7(); 

                }
                break;
            case 2 :
                // aql.g:1:15: T__8
                {
                mT__8(); 

                }
                break;
            case 3 :
                // aql.g:1:20: T__9
                {
                mT__9(); 

                }
                break;
            case 4 :
                // aql.g:1:25: T__10
                {
                mT__10(); 

                }
                break;
            case 5 :
                // aql.g:1:31: T__11
                {
                mT__11(); 

                }
                break;
            case 6 :
                // aql.g:1:37: T__12
                {
                mT__12(); 

                }
                break;
            case 7 :
                // aql.g:1:43: T__13
                {
                mT__13(); 

                }
                break;
            case 8 :
                // aql.g:1:49: T__14
                {
                mT__14(); 

                }
                break;
            case 9 :
                // aql.g:1:55: T__15
                {
                mT__15(); 

                }
                break;
            case 10 :
                // aql.g:1:61: T__16
                {
                mT__16(); 

                }
                break;
            case 11 :
                // aql.g:1:67: T__17
                {
                mT__17(); 

                }
                break;
            case 12 :
                // aql.g:1:73: T__18
                {
                mT__18(); 

                }
                break;
            case 13 :
                // aql.g:1:79: T__19
                {
                mT__19(); 

                }
                break;
            case 14 :
                // aql.g:1:85: QSTRINGTOKEN
                {
                mQSTRINGTOKEN(); 

                }
                break;
            case 15 :
                // aql.g:1:98: STRING
                {
                mSTRING(); 

                }
                break;
            case 16 :
                // aql.g:1:105: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\1\uffff\3\17\2\uffff\2\17\3\uffff\1\17\1\uffff\1\17\3\uffff\25"+
        "\17\1\55\6\17\1\uffff\1\64\2\17\1\67\1\70\1\17\1\uffff\2\17\2\uffff"+
        "\1\74\1\75\1\17\2\uffff\2\17\1\101\1\uffff";
    static final String DFA5_eofS =
        "\102\uffff";
    static final String DFA5_minS =
        "\1\11\1\117\1\114\1\121\2\uffff\1\101\1\105\3\uffff\1\105\1\uffff"+
        "\1\122\3\uffff\1\103\1\117\1\125\1\124\1\116\1\114\1\117\1\101\1"+
        "\102\1\111\1\125\1\101\1\105\1\112\1\114\1\101\1\112\1\122\1\115"+
        "\1\103\1\105\1\60\1\114\1\117\1\101\1\105\1\124\1\103\1\uffff\1"+
        "\60\1\111\1\114\2\60\1\124\1\uffff\1\116\1\112\2\uffff\2\60\1\117"+
        "\2\uffff\1\111\1\116\1\60\1\uffff";
    static final String DFA5_maxS =
        "\1\172\1\117\1\114\1\121\2\uffff\1\101\1\105\3\uffff\1\105\1\uffff"+
        "\1\122\3\uffff\1\103\1\117\1\125\1\124\1\116\1\114\1\117\1\101\1"+
        "\102\1\111\1\125\1\101\1\105\1\112\1\114\1\101\1\112\1\122\1\115"+
        "\1\103\1\105\1\172\1\114\1\117\1\101\1\105\1\124\1\103\1\uffff\1"+
        "\172\1\111\1\114\2\172\1\124\1\uffff\1\116\1\112\2\uffff\2\172\1"+
        "\117\2\uffff\1\111\1\116\1\172\1\uffff";
    static final String DFA5_acceptS =
        "\4\uffff\1\4\1\5\2\uffff\1\10\1\11\1\12\1\uffff\1\14\1\uffff\1\16"+
        "\1\17\1\20\34\uffff\1\1\6\uffff\1\2\2\uffff\1\7\1\13\3\uffff\1\15"+
        "\1\3\3\uffff\1\6";
    static final String DFA5_specialS =
        "\102\uffff}>";
    static final String[] DFA5_transitionS = {
            "\2\20\2\uffff\1\20\22\uffff\1\20\1\uffff\1\16\5\uffff\1\4\1"+
            "\5\3\uffff\1\11\2\uffff\12\17\3\uffff\1\14\3\uffff\4\17\1\3"+
            "\1\17\1\2\4\17\1\1\1\17\1\6\1\17\1\15\1\17\1\7\1\13\7\17\1\10"+
            "\1\uffff\1\12\3\uffff\32\17",
            "\1\21",
            "\1\22",
            "\1\23",
            "",
            "",
            "\1\24",
            "\1\25",
            "",
            "",
            "",
            "\1\26",
            "",
            "\1\27",
            "",
            "",
            "",
            "\1\30",
            "\1\31",
            "\1\32",
            "\1\33",
            "\1\34",
            "\1\35",
            "\1\36",
            "\1\37",
            "\1\40",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\44",
            "\1\45",
            "\1\46",
            "\1\47",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\12\17\7\uffff\32\17\6\uffff\32\17",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "",
            "\12\17\7\uffff\32\17\6\uffff\32\17",
            "\1\65",
            "\1\66",
            "\12\17\7\uffff\32\17\6\uffff\32\17",
            "\12\17\7\uffff\32\17\6\uffff\32\17",
            "\1\71",
            "",
            "\1\72",
            "\1\73",
            "",
            "",
            "\12\17\7\uffff\32\17\6\uffff\32\17",
            "\12\17\7\uffff\32\17\6\uffff\32\17",
            "\1\76",
            "",
            "",
            "\1\77",
            "\1\100",
            "\12\17\7\uffff\32\17\6\uffff\32\17",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__7 | T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | QSTRINGTOKEN | STRING | WS );";
        }
    }
 

}