// -----------------------------------------------------------------------
// <copyright file="LinkSmartEvent.cs" company="">
// TODO: Update copyright text.
// </copyright>
// -----------------------------------------------------------------------

namespace Components
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Internal representation of an event
    /// </summary>
    public class LinkSmartEvent
    {
        public LinkSmartEvent()
        {
            InternalId = Guid.Empty;
            this.Parts = new List<Part>();
        }

        public LinkSmartEvent(string eventTopic, Part[] eventContent) : base()
        {
            this.Topic = eventTopic;
            this.Parts = new List<Part>(eventContent);
        }
        public Guid InternalId {get;set;}
        public string Topic { get; set; }
        public List<Part> Parts { get; set; }
        public bool IsStored { get { return !Guid.Empty.Equals(InternalId); } }


        public DateTime Timestamp { get; set; }

        public int Priority { get; set; }
    }
}
