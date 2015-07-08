package com.pusher

import com.pusher.Types.{StringValidatorFunction, ListValidatorFunction}

/**
 * Custom types
 */
object Types {
  type PusherResponse = Either[PusherError, Map[String, Any]]

  type ValidationResponse = Option[ValidationError]

  type StringValidatorFunction = (String) => ValidationResponse

  type ListValidatorFunction = (List[String]) => ValidationResponse
}

sealed abstract class Validator
case class StringValidator(fn: StringValidatorFunction, arg: String) extends Validator
case class ListValidator(fn: ListValidatorFunction, arg: List[String]) extends Validator

/**
 * Custom type for Trigger data
 * @param channels List of channels
 * @param eventName Name of event
 * @param data Data to be sent
 * @param socketId SocketId if any
 */
case class TriggerData(channels: List[String],
                       eventName: String,
                       data: String,
                       socketId: Option[String])


/**
 * Config for Pusher
 * @param appId Pusher application id
 * @param key Pusher application key
 * @param secret Pusher application secret
 * @param ssl If SSL should be used or not
 * @param cluster The cluster to connect to
 * @param port The port to use when connecting
 * @param host Host to connect to
 */
case class PusherConfig(appId: String,
                        key: String,
                        secret: String,
                        ssl: Boolean = true,
                        cluster: Option[String] = None,
                        port: Option[Int] = None,
                        host: Option[String] = None) {
  /**
   * Get scheme based on ssl
   * @return String
   */
  def scheme: String = {
     ssl match {
      case true => "https"
      case false => "http"
    }
  }

  /**
   * Return the port based on config
   * @return Int
   */
  def getPort: Int = port.getOrElse(if (ssl) 443 else 80)

  /**
   * Returns the host config
   * @return String
   */
  def getHost: String = {
    host match {
      case Some(h) => h
      case None =>
        cluster match {
          case Some(c) => s"api-$c.pusher.com"
          case None => "api.pusherapp.com"
        }
    }
  }
}

/**
 * Request options
 * @param config PusherConfig object
 * @param verb Verb for the request
 * @param path Path to make the request to
 * @param params Optional parameters to be added to the request
 * @param body Optional body for the request
 */
case class RequestParams(config: PusherConfig,
                         verb: String,
                         path: String,
                         params: Option[Map[String, String]],
                         body: Option[String])
