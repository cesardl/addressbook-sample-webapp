<%--
    Document   : index
    Created on : 12/06/2010, 08:22:21 PM
    Author     : cesardl
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<f:view>
    <html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link type="image/x-icon" href="../images/favicon.ico" rel="icon">
        <link type="text/css" href="../css/addressbook.css" rel="stylesheet" media="screen"/>
        <title>Bienvenidos a la aplicacion</title>
    </head>
    <body>
    <h:form>
        <rich:panel style="border-style: none; margin:10% auto 0; width:390px;">
            <fieldset>
                <legend class="legendLogin">
                    <h:outputText value="Iniciar sesi&oacute;n" escape="false"/>
                </legend>

                        <table width="80%" align="center">
                            <tr>
                                <td colspan="2">
                                    <h:panelGroup rendered="#{managerLogin.error}" styleClass="error" layout="block">
                                        <h:graphicImage value="/images/caution.png" />
                                        <h:outputLabel value="Usuario y contrase&ntilde;a invalidos"
                                                       escape="false" styleClass="textError" />
                                    </h:panelGroup>
                                </td>
                            </tr>
                            <tr>
                                <td width="30%"><h:outputLabel value="Usuario:" styleClass="label"/></td>
                                <td><h:inputText value="#{managerLogin.usuario.usuUsuario}" style="width:100%"/></td>
                            </tr>
                            <tr>
                                <td><h:outputLabel value="Password:" styleClass="label"/></td>
                                <td><h:inputSecret value="#{managerLogin.usuario.usuPassword}" style="width:100%"/></td>
                            </tr>
                            <tr>
                                <td colspan="2" align="right">
                                    <h:commandButton value="Aceptar" style="width: 90px;"
                                                     action="#{managerLogin.validarUsuario}"/>
                                </td>
                            </tr>
                        </table>
                    </fieldset>
                </rich:panel>
            </h:form>
        </body>
    </html>
</f:view>
