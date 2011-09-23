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
// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g 2010-09-30 22:30:20

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ffoutputLexer extends Lexer {
    public static final int T__12=12;
    public static final int T__20=20;
    public static final int NUMBER=4;
    public static final int WS=7;
    public static final int T__13=13;
    public static final int T__19=19;
    public static final int T__9=9;
    public static final int T__14=14;
    public static final int T__11=11;
    public static final int T__17=17;
    public static final int T__8=8;
    public static final int EOF=-1;
    public static final int T__16=16;
    public static final int T__10=10;
    public static final int COLON=5;
    public static final int T__18=18;
    public static final int T__15=15;
    public static final int ID=6;

    // delegates
    // delegators

    public ffoutputLexer() {;} 
    public ffoutputLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ffoutputLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g"; }

    // $ANTLR start "T__8"
    public final void mT__8() throws RecognitionException {
        try {
            int _type = T__8;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:3:6: ( 'step' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:3:8: 'step'
            {
            match("step"); 


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
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:4:6: ( 'SETPROPERTY' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:4:8: 'SETPROPERTY'
            {
            match("SETPROPERTY"); 


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
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:5:7: ( 'STARTDEVICE' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:5:9: 'STARTDEVICE'
            {
            match("STARTDEVICE"); 


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
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:6:7: ( 'STOPDEVICE' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:6:9: 'STOPDEVICE'
            {
            match("STOPDEVICE"); 


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
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:7:7: ( 'STARTSERVICE' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:7:9: 'STARTSERVICE'
            {
            match("STARTSERVICE"); 


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
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:8:7: ( 'STOPSERVICE' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:8:9: 'STOPSERVICE'
            {
            match("STOPSERVICE"); 


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
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:9:7: ( 'DEPLOY' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:9:9: 'DEPLOY'
            {
            match("DEPLOY"); 


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
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:10:7: ( 'UNDEPLOY' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:10:9: 'UNDEPLOY'
            {
            match("UNDEPLOY"); 


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
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:11:7: ( 'INITDEVICE' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:11:9: 'INITDEVICE'
            {
            match("INITDEVICE"); 


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
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:12:7: ( 'INITCOMPONENT' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:12:9: 'INITCOMPONENT'
            {
            match("INITCOMPONENT"); 


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
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:13:7: ( 'INITSERVICE' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:13:9: 'INITSERVICE'
            {
            match("INITSERVICE"); 


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
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:14:7: ( 'BINDINTERFACE' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:14:9: 'BINDINTERFACE'
            {
            match("BINDINTERFACE"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:15:7: ( 'UNBINDINTERFACE' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:15:9: 'UNBINDINTERFACE'
            {
            match("UNBINDINTERFACE"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:12:7: ( ':' )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:12:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:14:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:14:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:14:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='-'||(LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:
            	    {
            	    if ( input.LA(1)=='-'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "NUMBER"
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:16:8: ( ( '0' .. '9' )+ )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:16:10: ( '0' .. '9' )+
            {
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:16:10: ( '0' .. '9' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:16:11: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

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
    // $ANTLR end "NUMBER"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:21:5: ( ( ' ' | '\\t' ) )
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:21:9: ( ' ' | '\\t' )
            {
            if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

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
        // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:8: ( T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | COLON | ID | NUMBER | WS )
        int alt3=17;
        alt3 = dfa3.predict(input);
        switch (alt3) {
            case 1 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:10: T__8
                {
                mT__8(); 

                }
                break;
            case 2 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:15: T__9
                {
                mT__9(); 

                }
                break;
            case 3 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:20: T__10
                {
                mT__10(); 

                }
                break;
            case 4 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:26: T__11
                {
                mT__11(); 

                }
                break;
            case 5 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:32: T__12
                {
                mT__12(); 

                }
                break;
            case 6 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:38: T__13
                {
                mT__13(); 

                }
                break;
            case 7 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:44: T__14
                {
                mT__14(); 

                }
                break;
            case 8 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:50: T__15
                {
                mT__15(); 

                }
                break;
            case 9 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:56: T__16
                {
                mT__16(); 

                }
                break;
            case 10 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:62: T__17
                {
                mT__17(); 

                }
                break;
            case 11 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:68: T__18
                {
                mT__18(); 

                }
                break;
            case 12 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:74: T__19
                {
                mT__19(); 

                }
                break;
            case 13 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:80: T__20
                {
                mT__20(); 

                }
                break;
            case 14 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:86: COLON
                {
                mCOLON(); 

                }
                break;
            case 15 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:92: ID
                {
                mID(); 

                }
                break;
            case 16 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:95: NUMBER
                {
                mNUMBER(); 

                }
                break;
            case 17 :
                // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:1:102: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA3 dfa3 = new DFA3(this);
    static final String DFA3_eotS =
        "\1\uffff\6\10\4\uffff\20\10\1\44\10\10\1\uffff\20\10\1\101\13\10"+
        "\1\uffff\13\10\1\130\12\10\1\uffff\10\10\1\153\2\10\1\156\3\10\1"+
        "\162\1\163\1\10\1\uffff\1\165\1\10\1\uffff\1\10\1\170\1\10\2\uffff"+
        "\1\172\1\uffff\2\10\1\uffff\1\10\1\uffff\1\10\1\177\1\u0080\1\10"+
        "\2\uffff\1\u0082\1\uffff";
    static final String DFA3_eofS =
        "\u0083\uffff";
    static final String DFA3_minS =
        "\1\11\1\164\2\105\2\116\1\111\4\uffff\1\145\1\124\1\101\1\120\1"+
        "\102\1\111\1\116\1\160\1\120\1\122\1\120\1\114\1\105\1\111\1\124"+
        "\1\104\1\55\1\122\1\124\1\104\1\117\1\120\1\116\1\103\1\111\1\uffff"+
        "\1\117\1\104\2\105\1\131\1\114\1\104\1\105\1\117\1\105\1\116\1\120"+
        "\2\105\1\126\1\122\1\55\1\117\1\111\1\126\1\115\1\122\1\124\1\105"+
        "\1\126\1\122\1\111\1\126\1\uffff\1\131\1\116\1\111\1\120\1\126\1"+
        "\105\1\122\1\111\1\126\1\103\1\111\1\55\1\124\1\103\1\117\1\111"+
        "\1\122\1\124\1\103\1\111\1\105\1\103\1\uffff\2\105\1\116\1\103\1"+
        "\106\1\131\1\105\1\103\1\55\1\105\1\122\1\55\2\105\1\101\2\55\1"+
        "\105\1\uffff\1\55\1\106\1\uffff\1\116\1\55\1\103\2\uffff\1\55\1"+
        "\uffff\1\101\1\124\1\uffff\1\105\1\uffff\1\103\2\55\1\105\2\uffff"+
        "\1\55\1\uffff";
    static final String DFA3_maxS =
        "\1\172\1\164\1\124\1\105\2\116\1\111\4\uffff\1\145\1\124\1\117\1"+
        "\120\1\104\1\111\1\116\1\160\1\120\1\122\1\120\1\114\1\105\1\111"+
        "\1\124\1\104\1\172\1\122\1\124\1\123\1\117\1\120\1\116\1\123\1\111"+
        "\1\uffff\1\117\1\123\2\105\1\131\1\114\1\104\1\105\1\117\1\105\1"+
        "\116\1\120\2\105\1\126\1\122\1\172\1\117\1\111\1\126\1\115\1\122"+
        "\1\124\1\105\1\126\1\122\1\111\1\126\1\uffff\1\131\1\116\1\111\1"+
        "\120\1\126\1\105\1\122\1\111\1\126\1\103\1\111\1\172\1\124\1\103"+
        "\1\117\1\111\1\122\1\124\1\103\1\111\1\105\1\103\1\uffff\2\105\1"+
        "\116\1\103\1\106\1\131\1\105\1\103\1\172\1\105\1\122\1\172\2\105"+
        "\1\101\2\172\1\105\1\uffff\1\172\1\106\1\uffff\1\116\1\172\1\103"+
        "\2\uffff\1\172\1\uffff\1\101\1\124\1\uffff\1\105\1\uffff\1\103\2"+
        "\172\1\105\2\uffff\1\172\1\uffff";
    static final String DFA3_acceptS =
        "\7\uffff\1\16\1\17\1\20\1\21\31\uffff\1\1\34\uffff\1\7\26\uffff"+
        "\1\10\22\uffff\1\4\2\uffff\1\11\3\uffff\1\2\1\3\1\uffff\1\6\2\uffff"+
        "\1\13\1\uffff\1\5\4\uffff\1\12\1\14\1\uffff\1\15";
    static final String DFA3_specialS =
        "\u0083\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\12\26\uffff\1\12\17\uffff\12\11\1\7\6\uffff\1\10\1\6\1\10"+
            "\1\3\4\10\1\5\11\10\1\2\1\10\1\4\5\10\4\uffff\1\10\1\uffff\22"+
            "\10\1\1\7\10",
            "\1\13",
            "\1\14\16\uffff\1\15",
            "\1\16",
            "\1\17",
            "\1\20",
            "\1\21",
            "",
            "",
            "",
            "",
            "\1\22",
            "\1\23",
            "\1\24\15\uffff\1\25",
            "\1\26",
            "\1\30\1\uffff\1\27",
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
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\45",
            "\1\46",
            "\1\47\16\uffff\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\55\1\54\16\uffff\1\56",
            "\1\57",
            "",
            "\1\60",
            "\1\61\16\uffff\1\62",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "\1\74",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\102",
            "\1\103",
            "\1\104",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "",
            "\1\115",
            "\1\116",
            "\1\117",
            "\1\120",
            "\1\121",
            "\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\154",
            "\1\155",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\164",
            "",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\166",
            "",
            "\1\167",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\171",
            "",
            "",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "",
            "\1\173",
            "\1\174",
            "",
            "\1\175",
            "",
            "\1\176",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\u0081",
            "",
            "",
            "\1\10\2\uffff\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            ""
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | COLON | ID | NUMBER | WS );";
        }
    }
 

}