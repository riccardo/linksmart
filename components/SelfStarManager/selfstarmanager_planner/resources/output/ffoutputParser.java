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

import org.antlr.runtime.debug.*;
import java.io.IOException;
public class ffoutputParser extends DebugParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NUMBER", "COLON", "ID", "WS", "'step'", "'SETPROPERTY'", "'STARTDEVICE'", "'STOPDEVICE'", "'STARTSERVICE'", "'STOPSERVICE'", "'DEPLOY'", "'UNDEPLOY'", "'INITDEVICE'", "'INITCOMPONENT'", "'INITSERVICE'", "'BINDINTERFACE'", "'UNBINDINTERFACE'"
    };
    public static final int T__12=12;
    public static final int T__20=20;
    public static final int WS=7;
    public static final int NUMBER=4;
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

    public static final String[] ruleNames = new String[] {
        "invalidRule", "start", "opcall", "operation", "cline", "ignore"
    };
     
        public int ruleLevel = 0;
        public int getRuleLevel() { return ruleLevel; }
        public void incRuleLevel() { ruleLevel++; }
        public void decRuleLevel() { ruleLevel--; }
        public ffoutputParser(TokenStream input) {
            this(input, DebugEventSocketProxy.DEFAULT_DEBUGGER_PORT, new RecognizerSharedState());
        }
        public ffoutputParser(TokenStream input, int port, RecognizerSharedState state) {
            super(input, state);
            DebugEventSocketProxy proxy =
                new DebugEventSocketProxy(this, port, null);
            setDebugListener(proxy);
            try {
                proxy.handshake();
            }
            catch (IOException ioe) {
                reportError(ioe);
            }
        }
    public ffoutputParser(TokenStream input, DebugEventListener dbg) {
        super(input, dbg, new RecognizerSharedState());

    }
    protected boolean evalPredicate(boolean result, String predicate) {
        dbg.semanticPredicate(result, predicate);
        return result;
    }


    public String[] getTokenNames() { return ffoutputParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g"; }



    // $ANTLR start "start"
    // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:3:1: start : ignore ( cline )+ ignore ;
    public final void start() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "start");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(3, 1);

        try {
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:3:7: ( ignore ( cline )+ ignore )
            dbg.enterAlt(1);

            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:3:9: ignore ( cline )+ ignore
            {
            dbg.location(3,9);
            pushFollow(FOLLOW_ignore_in_start10);
            ignore();

            state._fsp--;

            dbg.location(3,16);
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:3:16: ( cline )+
            int cnt1=0;
            try { dbg.enterSubRule(1);

            loop1:
            do {
                int alt1=2;
                try { dbg.enterDecision(1);

                int LA1_0 = input.LA(1);

                if ( (LA1_0==8) ) {
                    int LA1_1 = input.LA(2);

                    if ( (LA1_1==NUMBER) ) {
                        int LA1_3 = input.LA(3);

                        if ( (LA1_3==COLON) ) {
                            int LA1_4 = input.LA(4);

                            if ( ((LA1_4>=9 && LA1_4<=20)) ) {
                                int LA1_5 = input.LA(5);

                                if ( (LA1_5==ID) ) {
                                    alt1=1;
                                }


                            }


                        }


                    }


                }
                else if ( (LA1_0==NUMBER) ) {
                    int LA1_3 = input.LA(2);

                    if ( (LA1_3==COLON) ) {
                        int LA1_4 = input.LA(3);

                        if ( ((LA1_4>=9 && LA1_4<=20)) ) {
                            int LA1_5 = input.LA(4);

                            if ( (LA1_5==ID) ) {
                                alt1=1;
                            }


                        }


                    }


                }


                } finally {dbg.exitDecision(1);}

                switch (alt1) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:3:16: cline
            	    {
            	    dbg.location(3,16);
            	    pushFollow(FOLLOW_cline_in_start12);
            	    cline();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        dbg.recognitionException(eee);

                        throw eee;
                }
                cnt1++;
            } while (true);
            } finally {dbg.exitSubRule(1);}

            dbg.location(3,23);
            pushFollow(FOLLOW_ignore_in_start15);
            ignore();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(3, 29);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "start");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "start"


    // $ANTLR start "ignore"
    // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:4:1: ignore : ( . )* ;
    public final void ignore() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "ignore");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(4, 1);

        try {
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:4:8: ( ( . )* )
            dbg.enterAlt(1);

            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:4:10: ( . )*
            {
            dbg.location(4,10);
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:4:10: ( . )*
            try { dbg.enterSubRule(2);

            loop2:
            do {
                int alt2=2;
                try { dbg.enterDecision(2);

                switch ( input.LA(1) ) {
                case EOF:
                case 8:
                    {
                    alt2=2;
                    }
                    break;
                case NUMBER:
                    {
                    alt2=2;
                    }
                    break;
                case COLON:
                case ID:
                case WS:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                    {
                    alt2=1;
                    }
                    break;

                }

                } finally {dbg.exitDecision(2);}

                switch (alt2) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:4:10: .
            	    {
            	    dbg.location(4,10);
            	    matchAny(input); 

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);
            } finally {dbg.exitSubRule(2);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(4, 12);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "ignore");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "ignore"


    // $ANTLR start "cline"
    // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:6:1: cline : ( 'step' )? NUMBER COLON opcall ;
    public final void cline() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "cline");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(6, 1);

        try {
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:6:7: ( ( 'step' )? NUMBER COLON opcall )
            dbg.enterAlt(1);

            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:6:9: ( 'step' )? NUMBER COLON opcall
            {
            dbg.location(6,9);
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:6:9: ( 'step' )?
            int alt3=2;
            try { dbg.enterSubRule(3);
            try { dbg.enterDecision(3);

            int LA3_0 = input.LA(1);

            if ( (LA3_0==8) ) {
                alt3=1;
            }
            } finally {dbg.exitDecision(3);}

            switch (alt3) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:6:9: 'step'
                    {
                    dbg.location(6,9);
                    match(input,8,FOLLOW_8_in_cline31); 

                    }
                    break;

            }
            } finally {dbg.exitSubRule(3);}

            dbg.location(6,17);
            match(input,NUMBER,FOLLOW_NUMBER_in_cline34); 
            dbg.location(6,24);
            match(input,COLON,FOLLOW_COLON_in_cline36); 
            dbg.location(6,30);
            pushFollow(FOLLOW_opcall_in_cline38);
            opcall();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(6, 36);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "cline");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "cline"


    // $ANTLR start "opcall"
    // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:7:1: opcall : operation ( ID )+ ;
    public final void opcall() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "opcall");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(7, 1);

        try {
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:7:9: ( operation ( ID )+ )
            dbg.enterAlt(1);

            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:7:11: operation ( ID )+
            {
            dbg.location(7,11);
            pushFollow(FOLLOW_operation_in_opcall46);
            operation();

            state._fsp--;

            dbg.location(7,21);
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:7:21: ( ID )+
            int cnt4=0;
            try { dbg.enterSubRule(4);

            loop4:
            do {
                int alt4=2;
                try { dbg.enterDecision(4);

                int LA4_0 = input.LA(1);

                if ( (LA4_0==ID) ) {
                    alt4=1;
                }


                } finally {dbg.exitDecision(4);}

                switch (alt4) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:7:22: ID
            	    {
            	    dbg.location(7,22);
            	    match(input,ID,FOLLOW_ID_in_opcall49); 

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        dbg.recognitionException(eee);

                        throw eee;
                }
                cnt4++;
            } while (true);
            } finally {dbg.exitSubRule(4);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(7, 26);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "opcall");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "opcall"


    // $ANTLR start "operation"
    // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:8:1: operation : ( 'SETPROPERTY' | 'STARTDEVICE' | 'STOPDEVICE' | 'STARTSERVICE' | 'STOPSERVICE' | 'DEPLOY' | 'UNDEPLOY' | 'INITDEVICE' | 'INITCOMPONENT' | 'INITSERVICE' | 'BINDINTERFACE' | 'UNBINDINTERFACE' );
    public final void operation() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "operation");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(8, 1);

        try {
            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:9:2: ( 'SETPROPERTY' | 'STARTDEVICE' | 'STOPDEVICE' | 'STARTSERVICE' | 'STOPSERVICE' | 'DEPLOY' | 'UNDEPLOY' | 'INITDEVICE' | 'INITCOMPONENT' | 'INITSERVICE' | 'BINDINTERFACE' | 'UNBINDINTERFACE' )
            dbg.enterAlt(1);

            // /Users/ingstrup/Documents/workspaces/LinkSmart3/LinkSmartOS/components/SelfStarManager/selfstarmanager_planner/resources/ffoutput.g:
            {
            dbg.location(9,2);
            if ( (input.LA(1)>=9 && input.LA(1)<=20) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(10, 103);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "operation");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "operation"

    // Delegated rules


 

    public static final BitSet FOLLOW_ignore_in_start10 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_cline_in_start12 = new BitSet(new long[]{0x00000000001FFFF0L});
    public static final BitSet FOLLOW_ignore_in_start15 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_8_in_cline31 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_NUMBER_in_cline34 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_cline36 = new BitSet(new long[]{0x00000000001FFE00L});
    public static final BitSet FOLLOW_opcall_in_cline38 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operation_in_opcall46 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ID_in_opcall49 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_set_in_operation0 = new BitSet(new long[]{0x0000000000000002L});

}