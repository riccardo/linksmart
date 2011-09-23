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
﻿//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:4.0.30319.1
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Web.Services;
using System.Web.Services.Protocols;
using System.Xml.Serialization;

// 
// This source code was auto-generated by wsdl, Version=4.0.30319.1.
// 


/// <remarks/>
// CODEGEN: The optional WSDL extension element 'binding' from namespace 'http://IoT.eu.com/' was not handled.
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
[System.Web.Services.WebServiceBindingAttribute(Name="SMbind", Namespace="http://IoT.eu.com/")]
public partial class SM : System.Web.Services.Protocols.SoapHttpClientProtocol {
    
    private System.Threading.SendOrPostCallback createStorageDeviceOperationCompleted;
    
    private System.Threading.SendOrPostCallback createStorageDeviceLocalOperationCompleted;
    
    private System.Threading.SendOrPostCallback deleteStorageDeviceOperationCompleted;
    
    private System.Threading.SendOrPostCallback deleteStorageDeviceLocalOperationCompleted;
    
    private System.Threading.SendOrPostCallback getSupportedStorageDevicesOperationCompleted;
    
    private System.Threading.SendOrPostCallback getStorageDevicesOperationCompleted;
    
    private System.Threading.SendOrPostCallback getStorageDeviceConfigOperationCompleted;
    
    private System.Threading.SendOrPostCallback updateStorageDeviceOperationCompleted;
    
    private System.Threading.SendOrPostCallback updateStorageDeviceLocalOperationCompleted;
    
    /// <remarks/>
    public SM() {
        this.Url = "http://localhost:8083/services/storagemanager";
    }
    
    /// <remarks/>
    public event createStorageDeviceCompletedEventHandler createStorageDeviceCompleted;
    
    /// <remarks/>
    public event createStorageDeviceLocalCompletedEventHandler createStorageDeviceLocalCompleted;
    
    /// <remarks/>
    public event deleteStorageDeviceCompletedEventHandler deleteStorageDeviceCompleted;
    
    /// <remarks/>
    public event deleteStorageDeviceLocalCompletedEventHandler deleteStorageDeviceLocalCompleted;
    
    /// <remarks/>
    public event getSupportedStorageDevicesCompletedEventHandler getSupportedStorageDevicesCompleted;
    
    /// <remarks/>
    public event getStorageDevicesCompletedEventHandler getStorageDevicesCompleted;
    
    /// <remarks/>
    public event getStorageDeviceConfigCompletedEventHandler getStorageDeviceConfigCompleted;
    
    /// <remarks/>
    public event updateStorageDeviceCompletedEventHandler updateStorageDeviceCompleted;
    
    /// <remarks/>
    public event updateStorageDeviceLocalCompletedEventHandler updateStorageDeviceLocalCompleted;
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapRpcMethodAttribute("createFileSystemDevice", RequestNamespace="http://storagemanager.storage.IoT.eu.com", ResponseNamespace="http://storagemanager.storage.IoT.eu.com")]
    [return: System.Xml.Serialization.SoapElementAttribute("result")]
    public string createStorageDevice(string config) {
        object[] results = this.Invoke("createStorageDevice", new object[] {
                    config});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BegincreateStorageDevice(string config, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("createStorageDevice", new object[] {
                    config}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EndcreateStorageDevice(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void createStorageDeviceAsync(string config) {
        this.createStorageDeviceAsync(config, null);
    }
    
    /// <remarks/>
    public void createStorageDeviceAsync(string config, object userState) {
        if ((this.createStorageDeviceOperationCompleted == null)) {
            this.createStorageDeviceOperationCompleted = new System.Threading.SendOrPostCallback(this.OncreateStorageDeviceOperationCompleted);
        }
        this.InvokeAsync("createStorageDevice", new object[] {
                    config}, this.createStorageDeviceOperationCompleted, userState);
    }
    
    private void OncreateStorageDeviceOperationCompleted(object arg) {
        if ((this.createStorageDeviceCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.createStorageDeviceCompleted(this, new createStorageDeviceCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapRpcMethodAttribute("createFileSystemDeviceLocal", RequestNamespace="http://storagemanager.storage.IoT.eu.com", ResponseNamespace="http://storagemanager.storage.IoT.eu.com")]
    [return: System.Xml.Serialization.SoapElementAttribute("result")]
    public string createStorageDeviceLocal(string config) {
        object[] results = this.Invoke("createStorageDeviceLocal", new object[] {
                    config});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BegincreateStorageDeviceLocal(string config, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("createStorageDeviceLocal", new object[] {
                    config}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EndcreateStorageDeviceLocal(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void createStorageDeviceLocalAsync(string config) {
        this.createStorageDeviceLocalAsync(config, null);
    }
    
    /// <remarks/>
    public void createStorageDeviceLocalAsync(string config, object userState) {
        if ((this.createStorageDeviceLocalOperationCompleted == null)) {
            this.createStorageDeviceLocalOperationCompleted = new System.Threading.SendOrPostCallback(this.OncreateStorageDeviceLocalOperationCompleted);
        }
        this.InvokeAsync("createStorageDeviceLocal", new object[] {
                    config}, this.createStorageDeviceLocalOperationCompleted, userState);
    }
    
    private void OncreateStorageDeviceLocalOperationCompleted(object arg) {
        if ((this.createStorageDeviceLocalCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.createStorageDeviceLocalCompleted(this, new createStorageDeviceLocalCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapRpcMethodAttribute("deleteFileSystemDevice", RequestNamespace="http://storagemanager.storage.IoT.eu.com", ResponseNamespace="http://storagemanager.storage.IoT.eu.com")]
    [return: System.Xml.Serialization.SoapElementAttribute("result")]
    public string deleteStorageDevice(string id) {
        object[] results = this.Invoke("deleteStorageDevice", new object[] {
                    id});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BegindeleteStorageDevice(string id, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("deleteStorageDevice", new object[] {
                    id}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EnddeleteStorageDevice(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void deleteStorageDeviceAsync(string id) {
        this.deleteStorageDeviceAsync(id, null);
    }
    
    /// <remarks/>
    public void deleteStorageDeviceAsync(string id, object userState) {
        if ((this.deleteStorageDeviceOperationCompleted == null)) {
            this.deleteStorageDeviceOperationCompleted = new System.Threading.SendOrPostCallback(this.OndeleteStorageDeviceOperationCompleted);
        }
        this.InvokeAsync("deleteStorageDevice", new object[] {
                    id}, this.deleteStorageDeviceOperationCompleted, userState);
    }
    
    private void OndeleteStorageDeviceOperationCompleted(object arg) {
        if ((this.deleteStorageDeviceCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.deleteStorageDeviceCompleted(this, new deleteStorageDeviceCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapRpcMethodAttribute("deleteFileSystemDeviceLocal", RequestNamespace="http://storagemanager.storage.IoT.eu.com", ResponseNamespace="http://storagemanager.storage.IoT.eu.com")]
    [return: System.Xml.Serialization.SoapElementAttribute("result")]
    public string deleteStorageDeviceLocal(string filesystemname) {
        object[] results = this.Invoke("deleteStorageDeviceLocal", new object[] {
                    filesystemname});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BegindeleteStorageDeviceLocal(string filesystemname, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("deleteStorageDeviceLocal", new object[] {
                    filesystemname}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EnddeleteStorageDeviceLocal(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void deleteStorageDeviceLocalAsync(string filesystemname) {
        this.deleteStorageDeviceLocalAsync(filesystemname, null);
    }
    
    /// <remarks/>
    public void deleteStorageDeviceLocalAsync(string filesystemname, object userState) {
        if ((this.deleteStorageDeviceLocalOperationCompleted == null)) {
            this.deleteStorageDeviceLocalOperationCompleted = new System.Threading.SendOrPostCallback(this.OndeleteStorageDeviceLocalOperationCompleted);
        }
        this.InvokeAsync("deleteStorageDeviceLocal", new object[] {
                    filesystemname}, this.deleteStorageDeviceLocalOperationCompleted, userState);
    }
    
    private void OndeleteStorageDeviceLocalOperationCompleted(object arg) {
        if ((this.deleteStorageDeviceLocalCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.deleteStorageDeviceLocalCompleted(this, new deleteStorageDeviceLocalCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapRpcMethodAttribute("getSupportedFileSystemDevices", RequestNamespace="http://storagemanager.storage.IoT.eu.com", ResponseNamespace="http://storagemanager.storage.IoT.eu.com")]
    [return: System.Xml.Serialization.SoapElementAttribute("result")]
    public string getSupportedStorageDevices() {
        object[] results = this.Invoke("getSupportedStorageDevices", new object[0]);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BegingetSupportedStorageDevices(System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("getSupportedStorageDevices", new object[0], callback, asyncState);
    }
    
    /// <remarks/>
    public string EndgetSupportedStorageDevices(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void getSupportedStorageDevicesAsync() {
        this.getSupportedStorageDevicesAsync(null);
    }
    
    /// <remarks/>
    public void getSupportedStorageDevicesAsync(object userState) {
        if ((this.getSupportedStorageDevicesOperationCompleted == null)) {
            this.getSupportedStorageDevicesOperationCompleted = new System.Threading.SendOrPostCallback(this.OngetSupportedStorageDevicesOperationCompleted);
        }
        this.InvokeAsync("getSupportedStorageDevices", new object[0], this.getSupportedStorageDevicesOperationCompleted, userState);
    }
    
    private void OngetSupportedStorageDevicesOperationCompleted(object arg) {
        if ((this.getSupportedStorageDevicesCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.getSupportedStorageDevicesCompleted(this, new getSupportedStorageDevicesCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapRpcMethodAttribute("getFileSystemDevices", RequestNamespace="http://storagemanager.storage.IoT.eu.com", ResponseNamespace="http://storagemanager.storage.IoT.eu.com")]
    [return: System.Xml.Serialization.SoapElementAttribute("result")]
    public string getStorageDevices() {
        object[] results = this.Invoke("getStorageDevices", new object[0]);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BegingetStorageDevices(System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("getStorageDevices", new object[0], callback, asyncState);
    }
    
    /// <remarks/>
    public string EndgetStorageDevices(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void getStorageDevicesAsync() {
        this.getStorageDevicesAsync(null);
    }
    
    /// <remarks/>
    public void getStorageDevicesAsync(object userState) {
        if ((this.getStorageDevicesOperationCompleted == null)) {
            this.getStorageDevicesOperationCompleted = new System.Threading.SendOrPostCallback(this.OngetStorageDevicesOperationCompleted);
        }
        this.InvokeAsync("getStorageDevices", new object[0], this.getStorageDevicesOperationCompleted, userState);
    }
    
    private void OngetStorageDevicesOperationCompleted(object arg) {
        if ((this.getStorageDevicesCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.getStorageDevicesCompleted(this, new getStorageDevicesCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapRpcMethodAttribute("findFileSystemDevice", RequestNamespace="http://storagemanager.storage.IoT.eu.com", ResponseNamespace="http://storagemanager.storage.IoT.eu.com")]
    [return: System.Xml.Serialization.SoapElementAttribute("result")]
    public string getStorageDeviceConfig(string id) {
        object[] results = this.Invoke("getStorageDeviceConfig", new object[] {
                    id});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BegingetStorageDeviceConfig(string id, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("getStorageDeviceConfig", new object[] {
                    id}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EndgetStorageDeviceConfig(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void getStorageDeviceConfigAsync(string id) {
        this.getStorageDeviceConfigAsync(id, null);
    }
    
    /// <remarks/>
    public void getStorageDeviceConfigAsync(string id, object userState) {
        if ((this.getStorageDeviceConfigOperationCompleted == null)) {
            this.getStorageDeviceConfigOperationCompleted = new System.Threading.SendOrPostCallback(this.OngetStorageDeviceConfigOperationCompleted);
        }
        this.InvokeAsync("getStorageDeviceConfig", new object[] {
                    id}, this.getStorageDeviceConfigOperationCompleted, userState);
    }
    
    private void OngetStorageDeviceConfigOperationCompleted(object arg) {
        if ((this.getStorageDeviceConfigCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.getStorageDeviceConfigCompleted(this, new getStorageDeviceConfigCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapRpcMethodAttribute("updateFileSystemDevice", RequestNamespace="http://storagemanager.storage.IoT.eu.com", ResponseNamespace="http://storagemanager.storage.IoT.eu.com")]
    [return: System.Xml.Serialization.SoapElementAttribute("result")]
    public string updateStorageDevice(string config) {
        object[] results = this.Invoke("updateStorageDevice", new object[] {
                    config});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BeginupdateStorageDevice(string config, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("updateStorageDevice", new object[] {
                    config}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EndupdateStorageDevice(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void updateStorageDeviceAsync(string config) {
        this.updateStorageDeviceAsync(config, null);
    }
    
    /// <remarks/>
    public void updateStorageDeviceAsync(string config, object userState) {
        if ((this.updateStorageDeviceOperationCompleted == null)) {
            this.updateStorageDeviceOperationCompleted = new System.Threading.SendOrPostCallback(this.OnupdateStorageDeviceOperationCompleted);
        }
        this.InvokeAsync("updateStorageDevice", new object[] {
                    config}, this.updateStorageDeviceOperationCompleted, userState);
    }
    
    private void OnupdateStorageDeviceOperationCompleted(object arg) {
        if ((this.updateStorageDeviceCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.updateStorageDeviceCompleted(this, new updateStorageDeviceCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapRpcMethodAttribute("updateFileSystemDeviceLocal", RequestNamespace="http://storagemanager.storage.IoT.eu.com", ResponseNamespace="http://storagemanager.storage.IoT.eu.com")]
    [return: System.Xml.Serialization.SoapElementAttribute("result")]
    public string updateStorageDeviceLocal(string config) {
        object[] results = this.Invoke("updateStorageDeviceLocal", new object[] {
                    config});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BeginupdateStorageDeviceLocal(string config, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("updateStorageDeviceLocal", new object[] {
                    config}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EndupdateStorageDeviceLocal(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void updateStorageDeviceLocalAsync(string config) {
        this.updateStorageDeviceLocalAsync(config, null);
    }
    
    /// <remarks/>
    public void updateStorageDeviceLocalAsync(string config, object userState) {
        if ((this.updateStorageDeviceLocalOperationCompleted == null)) {
            this.updateStorageDeviceLocalOperationCompleted = new System.Threading.SendOrPostCallback(this.OnupdateStorageDeviceLocalOperationCompleted);
        }
        this.InvokeAsync("updateStorageDeviceLocal", new object[] {
                    config}, this.updateStorageDeviceLocalOperationCompleted, userState);
    }
    
    private void OnupdateStorageDeviceLocalOperationCompleted(object arg) {
        if ((this.updateStorageDeviceLocalCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.updateStorageDeviceLocalCompleted(this, new updateStorageDeviceLocalCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    public new void CancelAsync(object userState) {
        base.CancelAsync(userState);
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
public delegate void createStorageDeviceCompletedEventHandler(object sender, createStorageDeviceCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class createStorageDeviceCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal createStorageDeviceCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
public delegate void createStorageDeviceLocalCompletedEventHandler(object sender, createStorageDeviceLocalCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class createStorageDeviceLocalCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal createStorageDeviceLocalCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
public delegate void deleteStorageDeviceCompletedEventHandler(object sender, deleteStorageDeviceCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class deleteStorageDeviceCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal deleteStorageDeviceCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
public delegate void deleteStorageDeviceLocalCompletedEventHandler(object sender, deleteStorageDeviceLocalCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class deleteStorageDeviceLocalCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal deleteStorageDeviceLocalCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
public delegate void getSupportedStorageDevicesCompletedEventHandler(object sender, getSupportedStorageDevicesCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class getSupportedStorageDevicesCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal getSupportedStorageDevicesCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
public delegate void getStorageDevicesCompletedEventHandler(object sender, getStorageDevicesCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class getStorageDevicesCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal getStorageDevicesCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
public delegate void getStorageDeviceConfigCompletedEventHandler(object sender, getStorageDeviceConfigCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class getStorageDeviceConfigCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal getStorageDeviceConfigCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
public delegate void updateStorageDeviceCompletedEventHandler(object sender, updateStorageDeviceCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class updateStorageDeviceCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal updateStorageDeviceCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
public delegate void updateStorageDeviceLocalCompletedEventHandler(object sender, updateStorageDeviceLocalCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.1")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class updateStorageDeviceLocalCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal updateStorageDeviceLocalCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}
