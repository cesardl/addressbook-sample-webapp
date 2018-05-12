<%--
    Document   : actualizarContacto
    Created on : 10/03/2010, 09:32:03 AM
    Author     : cesardl
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j"%>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link type="image/x-icon" href="../images/favicon.ico" rel="icon">
        <link type="text/css" href="../css/addressbook.css" rel="stylesheet" media="screen"/>
        <title>Editar Contacto</title>
    </head>
    <body>
        <f:view>
            <div align="center">
                <rich:panel style="width: 55%">
                    <a4j:form id="fUpdate">
                        <table>
                            <tr>
                                <td colspan="2">
                                    <h:outputText value="<strong>#{managerAgenda.titulo}</strong>" escape="false"/></td>
                                <td align="right">
                                    <a4j:commandButton title="Guardar" image="/images/save.png"
                                                       action="#{managerAgenda.manipularContacto}"/></td>
                            </tr>
                            <tr>
                                <td colspan="3">
                                    <rich:separator height="3px" width="100%"/>
                                </td>
                            </tr>
                            <tr>
                                <td width="5%"><h:outputLabel for="id" value="Id:" styleClass="label"/></td>
                                <td><h:inputText id="id" value="#{managerAgenda.contacto.conId}" disabled="true"/></td>
                                <td rowspan="4">
                                    <rich:panel id="avatarContacto">
                                        <div align="center">
                                            <h:outputText value="No hay imagen cargada"
                                                          rendered="#{managerAgenda.contacto.conAvatar==null}" />
                                            <a4j:mediaOutput element="img" mimeType="#{managerAgenda.b_mime}"
                                                             createContent="#{managerAgenda.avatarContacto}"
                                                             value="vAvatar" cacheable="false"
                                                             style="width:100px; height:100px;">
                                                <f:param value="#{managerAgenda.timeStamp}" name="time"/>
                                            </a4j:mediaOutput>
                                            <br /><rich:spacer height="5px"/>
                                            <a4j:commandButton action="#{managerAgenda.limpiarAvatar}"
                                                               reRender="avatarContacto, avatar"
                                                               value="Borrar imagen"
                                                               rendered="#{managerAgenda.contacto.conAvatar!=null}" />
                                        </div>
                                    </rich:panel>
                                </td>
                            </tr>
                            <tr>
                                <td><h:outputLabel for="codigo" value="Codigo:" styleClass="label"/></td>
                                <td><h:inputText id="codigo" styleClass="input" required="true" maxlength="4"
                                             value="#{managerAgenda.contacto.conCodigo}">
                                        <f:validateLength minimum="4" maximum="4"/>
                                    </h:inputText>
                                    <rich:message for="codigo" styleClass="mensajeError"/></td>
                            </tr>
                            <tr>
                                <td><h:outputLabel for="nombre" value="Nombre:" styleClass="label" /></td>
                                <td><h:inputText id="nombre" styleClass="input" required="true"
                                             value="#{managerAgenda.contacto.conNombres}">
                                        <f:validateLength minimum="5" />
                                    </h:inputText>
                                    <rich:message for="nombre" styleClass="mensajeError"/>
                                </td>
                            </tr>
                            <tr>
                                <td><h:outputLabel for="telefono" value="Telefono:" styleClass="label" /></td>
                                <td><h:inputText id="telefono" styleClass="input" maxlength="9"
                                             value="#{managerAgenda.contacto.conTelefono}"/></td>
                            </tr>
                            <tr>
                                <td><h:outputLabel for="avatar" value="Avatar:" styleClass="label" /></td>
                                <td><rich:fileUpload id="avatar" maxFilesQuantity="1"
                                                 listHeight="58px" listWidth="300px"
                                                 immediateUpload="true"
                                                 acceptedTypes="jpg, jpeg, gif, JPG, JPEG ,GIF"
                                                 fileUploadListener="#{managerAgenda.cargarImagen}">
                                        <a4j:support event="onuploadcomplete" reRender="avatarContacto" />
                                    </rich:fileUpload></td>
                            </tr>
                            <tr>
                                <td><h:outputLabel for="email" value="E-mail:" styleClass="label" /></td>
                                <td><h:inputText id="email" styleClass="input"
                                             value="#{managerAgenda.contacto.conEmail}">
                                        <f:validator validatorId="emailValidator"/>
                                    </h:inputText>
                                    <rich:message for="email" styleClass="mensajeError"/>
                                </td>
                            </tr>
                            <tr>
                                <td><h:outputLabel for="cumpleanos:" value="Cumplea&ntilde;os"
                                               styleClass="label" escape="false"/></td>
                                <td><rich:calendar datePattern="dd - MM - yyyy" id="cumpleanos"
                                               value="#{managerAgenda.contacto.conCumpleanos}" /></td>
                            </tr>
                        </table>
                    </a4j:form>
                    <a4j:form id="fBack">
                        <div align="right">
                            <a4j:commandButton title="Regresar" action="TO_ADDRESS_BOOK"
                                               image="/images/back.png"/>
                        </div>
                    </a4j:form>
                </rich:panel>
            </div>
            <a4j:status onstart="Richfaces.showModalPanel('loading')"
                        onstop="Richfaces.hideModalPanel('loading')" />
            <rich:modalPanel
                id="loading" autosized="true" width="70" height="70"
                moveable="false" resizeable="false">
                <rich:panel>
                    <h:graphicImage value="/images/loading.gif" style="border:0" />
                </rich:panel>
            </rich:modalPanel>
        </f:view>
    </body>
</html>