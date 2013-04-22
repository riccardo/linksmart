// -----------------------------------------------------------------------
// <copyright file="EventManagerImplementation_2.cs" company="">
// TODO: Update copyright text.
// </copyright>
// -----------------------------------------------------------------------

namespace EventManager
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading;
    using System.ServiceModel;
    using System.Net;
    using System.Net.Sockets;
    using System.ServiceModel.Description;

    /// <summary>
    /// TODO: Update summary.
    /// </summary>
    public partial class EventManagerImplementation 
    {

        public bool publishXmlEvent(string eventXmlString)
        {
            //Make a copy of the list in order to avoid it being changed during the forea.ch loop. As long as copy is used, foreach can stay here. Otherwise -> Notification.cs
            //Parallel.ForEach(Components.Subscription subscription in Program.subscriptionList.Where(f => f.IsMatch(request.topic)).ToList(), 
            foreach (Components.Subscription subscription in EventManagerImplementation.subscriptionList.Where(f => f.IsMatch(eventXmlString)).ToList())
            {
                subscription.NumberOfRetries = 0;
                if (subscription.DateTime != null) { }
                else { subscription.DateTime = DateTime.Now; }
                Notification notification = new Notification(subscription, eventXmlString);
                Thread notificationThread = new Thread(new ThreadStart(notification.notify));
                notificationThread.Start();
            }   
            return true;
        }

    }
}

