<%--
    Document   : listaContacto
    Created on : 10/03/2010, 09:32:03 AM
    Author     : cesardl
--%>
<%
    HttpSession ses = request.getSession();
    if (ses.getAttribute("usuario") == null) {
%>
<jsp:forward page="../Login/login.jsp"/>
<% }
%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link type="image/x-icon" href="../images/favicon.ico" rel="icon">
    <link type="text/css" href="../css/addressbook.css" rel="stylesheet" media="screen"/>
    <title>Lista de contactos</title>
</head>
<body>
<f:view>
    <rich:panel header="Lista de contactos">
        <a4j:form id="action" ajaxSubmit="true">
            <table style="width: 100%">
                <tr>
                    <td align="left">
                        <strong>
                            <%@page import="org.sanmarcux.domain.Usuario" %>
                            <%
                                out.println("Bienvenido: " + ((Usuario) ses.getAttribute("usuario")).getUsuUsuario());
                            %>
                        </strong>
                    </td>
                    <td align="right">
                        <a4j:commandButton id="add" action="#{managerAgenda.nuevoContacto}"
                                           title="A&ntilde;adir contacto"
                                           image="/images/new.png"/>
                        <rich:spacer width="8px"/>

                        <a4j:commandButton id="find" action="#{managerAgenda.buscaContacto}"
                                           title="Buscar contacto"
                                           image="/images/search.png"/>
                        <rich:spacer width="8px"/>

                        <a4j:commandButton id="print"
                                           title="Imprimir" reRender="reporte"
                                           image="/images/print.png"
                                           oncomplete="Richfaces.showModalPanel('mpReporte', {top:50})"/>
                        <rich:spacer width="8px"/>

                        <a4j:commandButton id="help"
                                           title="Ayuda sobre la aplicacion"
                                           image="/images/help.png"
                                           oncomplete="Richfaces.showModalPanel('mpInfo', {top:130})"/>
                        <rich:spacer width="8px"/>

                        <a4j:commandButton id="closeSession"
                                           title="Cerrar sesion"
                                           image="/images/logout.png"
                                           action="#{managerLogin.cerrarSesion}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:separator height="3px" width="100%"/>
                    </td>
                </tr>
            </table>
        </a4j:form>
        <h:form id="data">
            <rich:datascroller id="scrollTableMaster" for="tablaMaster"
                               align="right" maxPages="5"/>
            <rich:dataTable id="tablaMaster" value="#{managerAgenda.lista}"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            var="contacto" rows="20" width="100%">

                <f:param id="contactId" name="id" value="#{contacto.conId}"/>

                <rich:column sortBy="#{contacto.conCodigo}" style="width: 9%; text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Codigo"/>
                    </f:facet>
                    <h:outputText value="#{contacto.conCodigo}"/>
                </rich:column>

                <rich:column sortBy="#{contacto.conNombres}" style="width: 30%;">
                    <f:facet name="header">
                        <h:outputText value="Nombres"/>
                    </f:facet>
                    <h:outputText value="#{contacto.conNombres}"/>
                </rich:column>

                <rich:column sortBy="#{contacto.conTelefono}" style="width: 10%; text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Telefono"/>
                    </f:facet>
                    <h:outputText value="#{contacto.conTelefono}"/>
                </rich:column>

                <rich:column sortBy="#{contacto.conEmail} " style="width: 15%;">
                    <f:facet name="header">
                        <h:outputText value="E-mail"/>
                    </f:facet>
                    <h:outputText value="#{contacto.conEmail}"/>
                </rich:column>

                <rich:column sortBy="#{contacto.conCumpleanos}" style="width: 15%;">
                    <f:facet name="header">
                        <h:outputText value="Cumplea&ntilde;os" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{contacto.conCumpleanos}">
                        <f:convertDateTime pattern="dd 'de' MMMMMM 'del' yyyy"/>
                    </h:outputText>
                </rich:column>

                <rich:column style="width: 7%; text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:commandButton action="#{managerAgenda.editarContacto}" image="/images/edit.png"
                                       actionListener="#{managerAgenda.seleccionarContacto}"/>
                </rich:column>

                <rich:column style="width: 7%; text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Ver detalle"/>
                    </f:facet>
                    <h:commandLink target="_blank" title="Ver detalle"
                                   actionListener="#{managerAgenda.verContacto}">
                        <h:graphicImage value="/images/find.png" style="border-style: none;"/>
                    </h:commandLink>
                </rich:column>

                <rich:column style="width: 7%; text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="/images/delete.png"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar a este contacto?'));"
                                     actionListener="#{managerAgenda.eliminarContacto}">
                        <a4j:support reRender="data, scrollTableMaster, tablaMaster"/>
                    </h:commandButton>
                </rich:column>
            </rich:dataTable>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpInfo" autosized="true" minWidth="100" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Acerca del AddressBook"/>
        </f:facet>
        <rich:panel onclick="Richfaces.hideModalPanel('mpInfo')"
                    style="border-style: none; text-align: center">
            <h:graphicImage styleClass="image" value="/images/mysql.png"
                            width="200px" height="105px"/>
            <h:outputText value="<h3>Este es un peque&ntilde;o ejemplo de algunos componentes de <i>RichFaces</i>
                                      , consultas a base de datos <i>MySQL</i> y generaci&oacute;n de
                                      reportes con <i>JasperReports</i>
                                      </h3>"
                          escape="false"/>
            <h:graphicImage styleClass="image" value="/images/richfaces.png"
                            width="300px" height="100px"/>
        </rich:panel>
    </rich:modalPanel>

    <rich:modalPanel id="mpReporte" autosized="true" zindex="4000">
        <f:facet name="header">
            <h:outputText value="Reporte de Contactos"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/images/close.png"
                            style="cursor: pointer;padding: 5px;"
                            onclick="Richfaces.hideModalPanel('mpReporte')"
                            title="Cerrar"/>
        </f:facet>
        <h:form>
            <div class="report">
                <rich:panel>
                    <a4j:mediaOutput id="reporte" uriAttribute="data"
                                     style="width : 650px; height: 500px;"
                                     element="object" standby="loading..."
                                     createContent="#{managerAgenda.reporteContactos}"
                                     mimeType="application/pdf"
                                     value="allContacts"/>
                </rich:panel>
            </div>
        </h:form>
    </rich:modalPanel>

    <a4j:status onstart="Richfaces.showModalPanel('loading')"
                onstop="Richfaces.hideModalPanel('loading')"/>
    <rich:modalPanel
            id="loading" autosized="true" width="70" height="70"
            moveable="false" resizeable="false">
        <rich:panel>
            <h:graphicImage value="/images/loading.gif" style="border:0"/>
        </rich:panel>
    </rich:modalPanel>
</f:view>
</body>
</html>