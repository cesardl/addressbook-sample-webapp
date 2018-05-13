<%-- 
    Document   : buscaContacto
    Created on : 10/03/2010, 09:32:03 AM
    Author     : cesardl
--%>
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
    <script type="text/javascript">
        /**
         function processObjectsChange(sgcomp, idCont, nomCont, mailCont, cumplCont) {
            var items = sgcomp.getSelectedItems();
            idCont.value = items.pluck('conId');
            nomCont.innerHTML = items.pluck('conNombres');
            mailCont.innerHTML = items.pluck('conEmail');
            cumplCont.innerHTML = items.pluck('conStrcumpl');
        }
         **/
    </script>
    <title>Busqueda de contacto</title>
</head>
<body>
<f:view>
    <div align="center">
        <rich:panel style="width: 40%;text-align: left;" header="Busca un contacto" headerClass="header">
            <h:form>
                <h:panelGrid style="width: 100%;text-align: center;"
                             columns="3" columnClasses="columns">
                    <%@page import="org.sanmarcux.domain.Usuario" %>
                    <%
                        HttpSession ses = request.getSession();
                        out.println("Usuario <b><i>" + ((Usuario) ses.getAttribute("usuario")).getUsuUsuario()
                                + "</i></b>, digite el nombre del contacto a buscar : ");
                    %>
                    <h:panelGroup>
                        <h:inputText id="suggest" styleClass="input"
                                     value="#{managerAgenda.sugstring}"/>
                        <a4j:jsFunction name="loadAvatar" actionListener="#{managerAgenda.loadAvatar}"
                                        reRender="avatCont"/>
                        <rich:suggestionbox id="suggestionCon" height="150" width="300"
                                            for="suggest" var="result" rules="none"
                                            suggestionAction="#{managerAgenda.autocomplete}"
                                            nothingLabel="No se encuentra contacto"
                                            cellspacing="2" reRender="data"
                                            shadowOpacity="4" shadowDepth="4"
                                            minChars="2" zindex="3500"
                                            usingSuggestObjects="true"
                                            onobjectchange="processObjectsChange(
                                                    #{rich:component('suggestionCon')},
                                                    #{rich:element('idCont')}, #{rich:element('nomCont')},
                                                    #{rich:element('mailCont')}, #{rich:element('cumplCont')});"
                                            onselect="loadAvatar();">
                            <h:column>
                                <h:outputText value="#{result.conNombres}"/>
                            </h:column>
                            <h:column>
                                <h:outputText value="#{result.conCodigo}"/>
                            </h:column>
                        </rich:suggestionbox>
                    </h:panelGroup>
                    <h:commandButton title="Aceptar" image="/images/correct.png"
                                     action="TO_ADDRESS_BOOK"/>
                </h:panelGrid>
                <rich:separator height="3px"/>
                <h:panelGroup id="data" layout="block">
                    <table>
                        <tr>
                            <td colspan="2" width="70%">
                                <h:inputHidden id="idCont" value="#{managerAgenda.sug_id}"/>
                                <f:param id="sugContactId" value="#{managerAgenda.sug_id}"/></td>
                            <td rowspan="4">
                                <rich:panel id="avatCont">
                                    <div align="center">
                                        <h:outputText value="No hay imagen encontrada"
                                                      rendered="#{managerAgenda.sug_avatar==null}"/>
                                        <a4j:mediaOutput element="img" mimeType="image/jpeg"
                                                         createContent="#{managerAgenda.avatarContacto}"
                                                         value="sugAvatar" cacheable="false"
                                                         style="width:100px; height:100px;"
                                                         rendered="#{managerAgenda.sug_avatar!=null}">
                                            <f:param value="#{managerAgenda.timeStamp}" name="time"/>
                                        </a4j:mediaOutput>
                                    </div>
                                </rich:panel>
                            </td>
                        </tr>
                        <tr>
                            <td><h:outputLabel value="Nombre:" styleClass="label"/></td>
                            <td><h:outputText id="nomCont"/></td>
                        </tr>
                        <tr>
                            <td><h:outputLabel value="E-mail:" styleClass="label"/></td>
                            <td><h:outputText id="mailCont"/></td>
                        </tr>
                        <tr>
                            <td><h:outputLabel value="Cumplea&ntilde;os:" styleClass="label" escape="false"/></td>
                            <td><h:outputText id="cumplCont"/></td>
                        </tr>
                    </table>
                </h:panelGroup>
            </h:form>
        </rich:panel>
    </div>
</f:view>
</body>
</html>