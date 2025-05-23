import React from "react"
import useAxios from "axios-hooks";
import { Entity } from "../models/Entity";
import { Instance, ProtoInstance } from "../models/instance";
import { ExplorerPaths, ManagementPaths, BASE_URL } from "../services/managementService";

export default function useModelsByInstance(params) {
  const[{data: instance}, getInstanceById] = useAxios<ProtoInstance>({
    baseURL: BASE_URL,
    method: "GET"
  }, {manual: true})

  const [{ data: models }, getModelsByParentId] = useAxios<Entity[]>({
    baseURL: BASE_URL,
    method: "GET",
  }, {manual: true});

  React.useEffect(() => {
    if (params) {
      if (params.instanceId) {
        getInstanceById({
          url: ManagementPaths.INSTANCE_BY_ID + params.instanceId,
        })
      }
    }
  }, [params])

  React.useEffect(() => {
    if (params.instanceId) {
      if (instance)
        getModelsByParentId({
            url: ExplorerPaths.MODELS + instance.entity.id
        });
    }
    else 
      getModelsByParentId({
            url: ExplorerPaths.MODELS
        });
  }, [instance, params])

  return {instance, models}
}