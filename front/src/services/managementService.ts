import Axios, { AxiosInstance } from "axios";

export const BASE_URL: string = "http://localhost:8080";

export enum ManagementPaths{
  BASE_MODEL = "/management/model",
  BASE_FIELD = "/management/field",
  FIELD_BY_MODEL_ID="/management/field/model/",
  INSTANCE_BY_ID = "/management/instance/"
}

export enum ExplorerPaths {
  MODELS="/explorer/models/",
  INSTANCE_VALUES_BY_ID="/explorer/instance/",
  INSTANCES_BY_MODEL_ID="/explorer/instances/",
  REALTIONS_BY_FIELD_ID="/explorer/instances/relation/"
}

export default function request(): AxiosInstance {
  return Axios.create({
    baseURL: BASE_URL,
    responseType: "json"
  });
}
