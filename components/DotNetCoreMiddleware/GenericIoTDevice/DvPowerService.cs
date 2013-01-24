using OpenSource.UPnP;

namespace IoT
{
    /// <summary>
    /// Transparent DeviceSide UPnP Service
    /// </summary>
    public class DvPowerService : IUPnPService
    {

        // Place your declarations above this line

        #region AutoGenerated Code Section [Do NOT Modify, unless you know what you're doing]
        //{{{{{ Begin Code Block

        private _DvPowerService _S;
        public static string URN = "urn:schemas-upnp-org:powerservice::1";
        public double VERSION
        {
           get
           {
               return(double.Parse(_S.GetUPnPService().Version));
           }
        }

        public delegate void OnStateVariableModifiedHandler(DvPowerService sender);
        public event OnStateVariableModifiedHandler OnStateVariableModified_BatteryOperated;
        public event OnStateVariableModifiedHandler OnStateVariableModified_MaxPowerConsumption;
        public event OnStateVariableModifiedHandler OnStateVariableModified_RemainingBatteryTime;
        public event OnStateVariableModifiedHandler OnStateVariableModified_StandByTime;
        public event OnStateVariableModifiedHandler OnStateVariableModified_CurrentPowerConsumption;
        public event OnStateVariableModifiedHandler OnStateVariableModified_ActiveTime;
        public event OnStateVariableModifiedHandler OnStateVariableModified_RemainingBatteryLevel;
        public System.Boolean BatteryOperated
        {
            get
            {
               return((System.Boolean)_S.GetStateVariable("BatteryOperated"));
            }
            set
            {
               _S.SetStateVariable("BatteryOperated", value);
            }
        }
        public System.String MaxPowerConsumption
        {
            get
            {
               return((System.String)_S.GetStateVariable("MaxPowerConsumption"));
            }
            set
            {
               _S.SetStateVariable("MaxPowerConsumption", value);
            }
        }
        public System.String RemainingBatteryTime
        {
            get
            {
               return((System.String)_S.GetStateVariable("RemainingBatteryTime"));
            }
            set
            {
               _S.SetStateVariable("RemainingBatteryTime", value);
            }
        }
        public System.String StandByTime
        {
            get
            {
               return((System.String)_S.GetStateVariable("StandByTime"));
            }
            set
            {
               _S.SetStateVariable("StandByTime", value);
            }
        }
        public System.String CurrentPowerConsumption
        {
            get
            {
               return((System.String)_S.GetStateVariable("CurrentPowerConsumption"));
            }
            set
            {
               _S.SetStateVariable("CurrentPowerConsumption", value);
            }
        }
        public System.String ActiveTime
        {
            get
            {
               return((System.String)_S.GetStateVariable("ActiveTime"));
            }
            set
            {
               _S.SetStateVariable("ActiveTime", value);
            }
        }
        public System.String RemainingBatteryLevel
        {
            get
            {
               return((System.String)_S.GetStateVariable("RemainingBatteryLevel"));
            }
            set
            {
               _S.SetStateVariable("RemainingBatteryLevel", value);
            }
        }
        public UPnPModeratedStateVariable.IAccumulator Accumulator_BatteryOperated
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("BatteryOperated")).Accumulator);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("BatteryOperated")).Accumulator = value;
            }
        }
        public double ModerationDuration_BatteryOperated
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("BatteryOperated")).ModerationPeriod);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("BatteryOperated")).ModerationPeriod = value;
            }
        }
        public UPnPModeratedStateVariable.IAccumulator Accumulator_MaxPowerConsumption
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("MaxPowerConsumption")).Accumulator);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("MaxPowerConsumption")).Accumulator = value;
            }
        }
        public double ModerationDuration_MaxPowerConsumption
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("MaxPowerConsumption")).ModerationPeriod);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("MaxPowerConsumption")).ModerationPeriod = value;
            }
        }
        public UPnPModeratedStateVariable.IAccumulator Accumulator_RemainingBatteryTime
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("RemainingBatteryTime")).Accumulator);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("RemainingBatteryTime")).Accumulator = value;
            }
        }
        public double ModerationDuration_RemainingBatteryTime
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("RemainingBatteryTime")).ModerationPeriod);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("RemainingBatteryTime")).ModerationPeriod = value;
            }
        }
        public UPnPModeratedStateVariable.IAccumulator Accumulator_StandByTime
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("StandByTime")).Accumulator);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("StandByTime")).Accumulator = value;
            }
        }
        public double ModerationDuration_StandByTime
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("StandByTime")).ModerationPeriod);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("StandByTime")).ModerationPeriod = value;
            }
        }
        public UPnPModeratedStateVariable.IAccumulator Accumulator_CurrentPowerConsumption
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("CurrentPowerConsumption")).Accumulator);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("CurrentPowerConsumption")).Accumulator = value;
            }
        }
        public double ModerationDuration_CurrentPowerConsumption
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("CurrentPowerConsumption")).ModerationPeriod);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("CurrentPowerConsumption")).ModerationPeriod = value;
            }
        }
        public UPnPModeratedStateVariable.IAccumulator Accumulator_ActiveTime
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("ActiveTime")).Accumulator);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("ActiveTime")).Accumulator = value;
            }
        }
        public double ModerationDuration_ActiveTime
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("ActiveTime")).ModerationPeriod);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("ActiveTime")).ModerationPeriod = value;
            }
        }
        public UPnPModeratedStateVariable.IAccumulator Accumulator_RemainingBatteryLevel
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("RemainingBatteryLevel")).Accumulator);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("RemainingBatteryLevel")).Accumulator = value;
            }
        }
        public double ModerationDuration_RemainingBatteryLevel
        {
            get
            {
                 return(((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("RemainingBatteryLevel")).ModerationPeriod);
            }
            set
            {
                 ((UPnPModeratedStateVariable)_S.GetUPnPService().GetStateVariableObject("RemainingBatteryLevel")).ModerationPeriod = value;
            }
        }
        public delegate System.String Delegate_GetCurrentConsumption();
        public delegate System.String Delegate_GetMaxPowerConsumption();
        public delegate System.String Delegate_GetRemainingBatteryLevel();
        public delegate System.String Delegate_GetRemainingBatteryTime();
        public delegate System.String Delegate_GetStandByTime();
        public delegate System.String Delegate_GetTimeActive();
        public delegate System.String Delegate_GetTimeUntilStandBy();
        public delegate System.Boolean Delegate_IsBatteryOperated();
        public delegate void Delegate_SetStandByTime(System.String StandByTime);

        public Delegate_GetCurrentConsumption External_GetCurrentConsumption = null;
        public Delegate_GetMaxPowerConsumption External_GetMaxPowerConsumption = null;
        public Delegate_GetRemainingBatteryLevel External_GetRemainingBatteryLevel = null;
        public Delegate_GetRemainingBatteryTime External_GetRemainingBatteryTime = null;
        public Delegate_GetStandByTime External_GetStandByTime = null;
        public Delegate_GetTimeActive External_GetTimeActive = null;
        public Delegate_GetTimeUntilStandBy External_GetTimeUntilStandBy = null;
        public Delegate_IsBatteryOperated External_IsBatteryOperated = null;
        public Delegate_SetStandByTime External_SetStandByTime = null;

        public void RemoveStateVariable_BatteryOperated()
        {
            _S.GetUPnPService().RemoveStateVariable(_S.GetUPnPService().GetStateVariableObject("BatteryOperated"));
        }
        public void RemoveStateVariable_MaxPowerConsumption()
        {
            _S.GetUPnPService().RemoveStateVariable(_S.GetUPnPService().GetStateVariableObject("MaxPowerConsumption"));
        }
        public void RemoveStateVariable_RemainingBatteryTime()
        {
            _S.GetUPnPService().RemoveStateVariable(_S.GetUPnPService().GetStateVariableObject("RemainingBatteryTime"));
        }
        public void RemoveStateVariable_StandByTime()
        {
            _S.GetUPnPService().RemoveStateVariable(_S.GetUPnPService().GetStateVariableObject("StandByTime"));
        }
        public void RemoveStateVariable_CurrentPowerConsumption()
        {
            _S.GetUPnPService().RemoveStateVariable(_S.GetUPnPService().GetStateVariableObject("CurrentPowerConsumption"));
        }
        public void RemoveStateVariable_ActiveTime()
        {
            _S.GetUPnPService().RemoveStateVariable(_S.GetUPnPService().GetStateVariableObject("ActiveTime"));
        }
        public void RemoveStateVariable_RemainingBatteryLevel()
        {
            _S.GetUPnPService().RemoveStateVariable(_S.GetUPnPService().GetStateVariableObject("RemainingBatteryLevel"));
        }
        public void RemoveAction_GetCurrentConsumption()
        {
             _S.GetUPnPService().RemoveMethod("GetCurrentConsumption");
        }
        public void RemoveAction_GetMaxPowerConsumption()
        {
             _S.GetUPnPService().RemoveMethod("GetMaxPowerConsumption");
        }
        public void RemoveAction_GetRemainingBatteryLevel()
        {
             _S.GetUPnPService().RemoveMethod("GetRemainingBatteryLevel");
        }
        public void RemoveAction_GetRemainingBatteryTime()
        {
             _S.GetUPnPService().RemoveMethod("GetRemainingBatteryTime");
        }
        public void RemoveAction_GetStandByTime()
        {
             _S.GetUPnPService().RemoveMethod("GetStandByTime");
        }
        public void RemoveAction_GetTimeActive()
        {
             _S.GetUPnPService().RemoveMethod("GetTimeActive");
        }
        public void RemoveAction_GetTimeUntilStandBy()
        {
             _S.GetUPnPService().RemoveMethod("GetTimeUntilStandBy");
        }
        public void RemoveAction_IsBatteryOperated()
        {
             _S.GetUPnPService().RemoveMethod("IsBatteryOperated");
        }
        public void RemoveAction_SetStandByTime()
        {
             _S.GetUPnPService().RemoveMethod("SetStandByTime");
        }
        public System.Net.IPEndPoint GetCaller()
        {
             return(_S.GetUPnPService().GetCaller());
        }
        public System.Net.IPEndPoint GetReceiver()
        {
             return(_S.GetUPnPService().GetReceiver());
        }

        private class _DvPowerService
        {
            private DvPowerService Outer = null;
            private UPnPService S;
            internal _DvPowerService(DvPowerService n)
            {
                Outer = n;
                S = BuildUPnPService();
            }
            public UPnPService GetUPnPService()
            {
                return(S);
            }
            public void SetStateVariable(string VarName, object VarValue)
            {
               S.SetStateVariable(VarName,VarValue);
            }
            public object GetStateVariable(string VarName)
            {
               return(S.GetStateVariable(VarName));
            }
            protected UPnPService BuildUPnPService()
            {
                UPnPStateVariable[] RetVal = new UPnPStateVariable[7];
                RetVal[0] = new UPnPModeratedStateVariable("BatteryOperated", typeof(System.Boolean), false);
                RetVal[0].AddAssociation("IsBatteryOperated", "BatteryOperated");
                RetVal[1] = new UPnPModeratedStateVariable("MaxPowerConsumption", typeof(System.String), false);
                RetVal[1].AddAssociation("GetMaxPowerConsumption", "MaxPowerConsumption");
                RetVal[2] = new UPnPModeratedStateVariable("RemainingBatteryTime", typeof(System.String), false);
                RetVal[2].AddAssociation("GetRemainingBatteryTime", "RemainingBatteryTime");
                RetVal[3] = new UPnPModeratedStateVariable("StandByTime", typeof(System.String), false);
                RetVal[3].AddAssociation("GetStandByTime", "StandByTime");
                RetVal[3].AddAssociation("GetTimeUntilStandBy", "TimeUntilStandBy");
                RetVal[3].AddAssociation("SetStandByTime", "StandByTime");
                RetVal[4] = new UPnPModeratedStateVariable("CurrentPowerConsumption", typeof(System.String), false);
                RetVal[4].AddAssociation("GetCurrentConsumption", "CurrentPowerConsumption");
                RetVal[5] = new UPnPModeratedStateVariable("ActiveTime", typeof(System.String), false);
                RetVal[5].AddAssociation("GetTimeActive", "ActiveTime");
                RetVal[6] = new UPnPModeratedStateVariable("RemainingBatteryLevel", typeof(System.String), false);
                RetVal[6].AddAssociation("GetRemainingBatteryLevel", "ReaminingBatteryLevel");

                UPnPService S = new UPnPService(1, "urn:upnp-org:serviceId:powerservice:1", "urn:schemas-upnp-org:powerservice::1", true, this);
                for(int i=0;i<RetVal.Length;++i)
                {
                   S.AddStateVariable(RetVal[i]);
                }
                S.AddMethod("GetCurrentConsumption");
                S.AddMethod("GetMaxPowerConsumption");
                S.AddMethod("GetRemainingBatteryLevel");
                S.AddMethod("GetRemainingBatteryTime");
                S.AddMethod("GetStandByTime");
                S.AddMethod("GetTimeActive");
                S.AddMethod("GetTimeUntilStandBy");
                S.AddMethod("IsBatteryOperated");
                S.AddMethod("SetStandByTime");
                return(S);
            }

            [OpenSource.UPnP.ReturnArgument("CurrentPowerConsumption")]
            public System.String GetCurrentConsumption()
            {
                object RetObj = null;
                if(Outer.External_GetCurrentConsumption != null)
                {
                    RetObj = Outer.External_GetCurrentConsumption();
                }
                else
                {
                    RetObj = Sink_GetCurrentConsumption();
                }
                return((System.String)RetObj);
            }
            [OpenSource.UPnP.ReturnArgument("MaxPowerConsumption")]
            public System.String GetMaxPowerConsumption()
            {
                object RetObj = null;
                if(Outer.External_GetMaxPowerConsumption != null)
                {
                    RetObj = Outer.External_GetMaxPowerConsumption();
                }
                else
                {
                    RetObj = Sink_GetMaxPowerConsumption();
                }
                return((System.String)RetObj);
            }
            [OpenSource.UPnP.ReturnArgument("ReaminingBatteryLevel")]
            public System.String GetRemainingBatteryLevel()
            {
                object RetObj = null;
                if(Outer.External_GetRemainingBatteryLevel != null)
                {
                    RetObj = Outer.External_GetRemainingBatteryLevel();
                }
                else
                {
                    RetObj = Sink_GetRemainingBatteryLevel();
                }
                return((System.String)RetObj);
            }
            [OpenSource.UPnP.ReturnArgument("RemainingBatteryTime")]
            public System.String GetRemainingBatteryTime()
            {
                object RetObj = null;
                if(Outer.External_GetRemainingBatteryTime != null)
                {
                    RetObj = Outer.External_GetRemainingBatteryTime();
                }
                else
                {
                    RetObj = Sink_GetRemainingBatteryTime();
                }
                return((System.String)RetObj);
            }
            [OpenSource.UPnP.ReturnArgument("StandByTime")]
            public System.String GetStandByTime()
            {
                object RetObj = null;
                if(Outer.External_GetStandByTime != null)
                {
                    RetObj = Outer.External_GetStandByTime();
                }
                else
                {
                    RetObj = Sink_GetStandByTime();
                }
                return((System.String)RetObj);
            }
            [OpenSource.UPnP.ReturnArgument("ActiveTime")]
            public System.String GetTimeActive()
            {
                object RetObj = null;
                if(Outer.External_GetTimeActive != null)
                {
                    RetObj = Outer.External_GetTimeActive();
                }
                else
                {
                    RetObj = Sink_GetTimeActive();
                }
                return((System.String)RetObj);
            }
            [OpenSource.UPnP.ReturnArgument("TimeUntilStandBy")]
            public System.String GetTimeUntilStandBy()
            {
                object RetObj = null;
                if(Outer.External_GetTimeUntilStandBy != null)
                {
                    RetObj = Outer.External_GetTimeUntilStandBy();
                }
                else
                {
                    RetObj = Sink_GetTimeUntilStandBy();
                }
                return((System.String)RetObj);
            }
            [OpenSource.UPnP.ReturnArgument("BatteryOperated")]
            public System.Boolean IsBatteryOperated()
            {
                object RetObj = null;
                if(Outer.External_IsBatteryOperated != null)
                {
                    RetObj = Outer.External_IsBatteryOperated();
                }
                else
                {
                    RetObj = Sink_IsBatteryOperated();
                }
                return((System.Boolean)RetObj);
            }
            public void SetStandByTime(System.String StandByTime)
            {
                if(Outer.External_SetStandByTime != null)
                {
                    Outer.External_SetStandByTime(StandByTime);
                }
                else
                {
                    Sink_SetStandByTime(StandByTime);
                }
            }

            public Delegate_GetCurrentConsumption Sink_GetCurrentConsumption;
            public Delegate_GetMaxPowerConsumption Sink_GetMaxPowerConsumption;
            public Delegate_GetRemainingBatteryLevel Sink_GetRemainingBatteryLevel;
            public Delegate_GetRemainingBatteryTime Sink_GetRemainingBatteryTime;
            public Delegate_GetStandByTime Sink_GetStandByTime;
            public Delegate_GetTimeActive Sink_GetTimeActive;
            public Delegate_GetTimeUntilStandBy Sink_GetTimeUntilStandBy;
            public Delegate_IsBatteryOperated Sink_IsBatteryOperated;
            public Delegate_SetStandByTime Sink_SetStandByTime;
        }
        public DvPowerService()
        {
            _S = new _DvPowerService(this);
            _S.GetUPnPService().GetStateVariableObject("BatteryOperated").OnModified += new UPnPStateVariable.ModifiedHandler(OnModifiedSink_BatteryOperated);
            _S.GetUPnPService().GetStateVariableObject("MaxPowerConsumption").OnModified += new UPnPStateVariable.ModifiedHandler(OnModifiedSink_MaxPowerConsumption);
            _S.GetUPnPService().GetStateVariableObject("RemainingBatteryTime").OnModified += new UPnPStateVariable.ModifiedHandler(OnModifiedSink_RemainingBatteryTime);
            _S.GetUPnPService().GetStateVariableObject("StandByTime").OnModified += new UPnPStateVariable.ModifiedHandler(OnModifiedSink_StandByTime);
            _S.GetUPnPService().GetStateVariableObject("CurrentPowerConsumption").OnModified += new UPnPStateVariable.ModifiedHandler(OnModifiedSink_CurrentPowerConsumption);
            _S.GetUPnPService().GetStateVariableObject("ActiveTime").OnModified += new UPnPStateVariable.ModifiedHandler(OnModifiedSink_ActiveTime);
            _S.GetUPnPService().GetStateVariableObject("RemainingBatteryLevel").OnModified += new UPnPStateVariable.ModifiedHandler(OnModifiedSink_RemainingBatteryLevel);
            _S.Sink_GetCurrentConsumption = new Delegate_GetCurrentConsumption(GetCurrentConsumption);
            _S.Sink_GetMaxPowerConsumption = new Delegate_GetMaxPowerConsumption(GetMaxPowerConsumption);
            _S.Sink_GetRemainingBatteryLevel = new Delegate_GetRemainingBatteryLevel(GetRemainingBatteryLevel);
            _S.Sink_GetRemainingBatteryTime = new Delegate_GetRemainingBatteryTime(GetRemainingBatteryTime);
            _S.Sink_GetStandByTime = new Delegate_GetStandByTime(GetStandByTime);
            _S.Sink_GetTimeActive = new Delegate_GetTimeActive(GetTimeActive);
            _S.Sink_GetTimeUntilStandBy = new Delegate_GetTimeUntilStandBy(GetTimeUntilStandBy);
            _S.Sink_IsBatteryOperated = new Delegate_IsBatteryOperated(IsBatteryOperated);
            _S.Sink_SetStandByTime = new Delegate_SetStandByTime(SetStandByTime);
        }
        public DvPowerService(string ID):this()
        {
            _S.GetUPnPService().ServiceID = ID;
        }
        public UPnPService GetUPnPService()
        {
            return(_S.GetUPnPService());
        }
        private void OnModifiedSink_BatteryOperated(UPnPStateVariable sender, object NewValue)
        {
            if(OnStateVariableModified_BatteryOperated != null) OnStateVariableModified_BatteryOperated(this);
        }
        private void OnModifiedSink_MaxPowerConsumption(UPnPStateVariable sender, object NewValue)
        {
            if(OnStateVariableModified_MaxPowerConsumption != null) OnStateVariableModified_MaxPowerConsumption(this);
        }
        private void OnModifiedSink_RemainingBatteryTime(UPnPStateVariable sender, object NewValue)
        {
            if(OnStateVariableModified_RemainingBatteryTime != null) OnStateVariableModified_RemainingBatteryTime(this);
        }
        private void OnModifiedSink_StandByTime(UPnPStateVariable sender, object NewValue)
        {
            if(OnStateVariableModified_StandByTime != null) OnStateVariableModified_StandByTime(this);
        }
        private void OnModifiedSink_CurrentPowerConsumption(UPnPStateVariable sender, object NewValue)
        {
            if(OnStateVariableModified_CurrentPowerConsumption != null) OnStateVariableModified_CurrentPowerConsumption(this);
        }
        private void OnModifiedSink_ActiveTime(UPnPStateVariable sender, object NewValue)
        {
            if(OnStateVariableModified_ActiveTime != null) OnStateVariableModified_ActiveTime(this);
        }
        private void OnModifiedSink_RemainingBatteryLevel(UPnPStateVariable sender, object NewValue)
        {
            if(OnStateVariableModified_RemainingBatteryLevel != null) OnStateVariableModified_RemainingBatteryLevel(this);
        }
        //}}}}} End of Code Block

        #endregion

        /// <summary>
        /// Action: GetCurrentConsumption
        /// </summary>
        /// <returns>Associated StateVariable: CurrentPowerConsumption</returns>
        public System.String GetCurrentConsumption()
        {
            //ToDo: Add Your implementation here, and remove exception
            throw(new UPnPCustomException(800,"This method has not been completely implemented..."));
        }
        /// <summary>
        /// Action: GetMaxPowerConsumption
        /// </summary>
        /// <returns>Associated StateVariable: MaxPowerConsumption</returns>
        public System.String GetMaxPowerConsumption()
        {
            //ToDo: Add Your implementation here, and remove exception
            throw(new UPnPCustomException(800,"This method has not been completely implemented..."));
        }
        /// <summary>
        /// Action: GetRemainingBatteryLevel
        /// </summary>
        /// <returns>Associated StateVariable: RemainingBatteryLevel</returns>
        public System.String GetRemainingBatteryLevel()
        {
            //ToDo: Add Your implementation here, and remove exception
            throw(new UPnPCustomException(800,"This method has not been completely implemented..."));
        }
        /// <summary>
        /// Action: GetRemainingBatteryTime
        /// </summary>
        /// <returns>Associated StateVariable: RemainingBatteryTime</returns>
        public System.String GetRemainingBatteryTime()
        {
            //ToDo: Add Your implementation here, and remove exception
            throw(new UPnPCustomException(800,"This method has not been completely implemented..."));
        }
        /// <summary>
        /// Action: GetStandByTime
        /// </summary>
        /// <returns>Associated StateVariable: StandByTime</returns>
        public System.String GetStandByTime()
        {
            //ToDo: Add Your implementation here, and remove exception
            throw(new UPnPCustomException(800,"This method has not been completely implemented..."));
        }
        /// <summary>
        /// Action: GetTimeActive
        /// </summary>
        /// <returns>Associated StateVariable: ActiveTime</returns>
        public System.String GetTimeActive()
        {
            //ToDo: Add Your implementation here, and remove exception
            throw(new UPnPCustomException(800,"This method has not been completely implemented..."));
        }
        /// <summary>
        /// Action: GetTimeUntilStandBy
        /// </summary>
        /// <returns>Associated StateVariable: StandByTime</returns>
        public System.String GetTimeUntilStandBy()
        {
            //ToDo: Add Your implementation here, and remove exception
            throw(new UPnPCustomException(800,"This method has not been completely implemented..."));
        }
        /// <summary>
        /// Action: IsBatteryOperated
        /// </summary>
        /// <returns>Associated StateVariable: BatteryOperated</returns>
        public System.Boolean IsBatteryOperated()
        {
            //ToDo: Add Your implementation here, and remove exception
            throw(new UPnPCustomException(800,"This method has not been completely implemented..."));
        }
        /// <summary>
        /// Action: SetStandByTime
        /// </summary>
        /// <param name="StandByTime">Associated State Variable: StandByTime</param>
        public void SetStandByTime(System.String StandByTime)
        {
            //ToDo: Add Your implementation here, and remove exception
            throw(new UPnPCustomException(800,"This method has not been completely implemented..."));
        }
    }
}