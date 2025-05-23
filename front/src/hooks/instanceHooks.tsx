import React from "react"
import { Instance, InstanceData } from "../models/instance";
import { Field, Type } from "../models/Field";

export default function useInstanceDataForm(
    instanceData: InstanceData[], 
    instanceName?: string) {
    function init(): Instance {
        let bufferInstance: Instance = {
            name: "",
            data: new Map<number, string>(),
            relations: new Map<number, number[]>(),
            deleteRelations: new Map<number, number[]>(),
        };
        instanceName
            ? (bufferInstance.name = instanceName)
            : (bufferInstance.name = "");

        if (instanceData) {
            instanceData.forEach((field) => {
            if (
                (field.fieldType == Type.MULTIPLE_RELATION ||
                field.fieldType == Type.RELATION) &&
                Array.isArray(field.value) &&
                field.id
            ) {
                
                let rels: number[] = field.value.map((rel) => rel.id!);
                bufferInstance.relations!.set(field.id, rels);
            } else {
                if (typeof field.value == "string" && field.id)
                bufferInstance.data!.set(field.id, field.value);
            }
            });
        }
        return bufferInstance;
        };
        
        const [instance, setInstance] = React.useState<Instance>(init);
    
        React.useEffect(() => {
        setInstance(init);
        }, [instanceData]);
    
        const updateDataHandler = React.useCallback((value, id) => {
            instance.data!.set(id, value);
            setInstance((state) => {
            return {
                ...state,
                data: instance.data,
            };
            });
        }, [instance]);
    
        const updateRelationHandler = React.useCallback((value, id) => {
        let oldRelations: number[] | undefined = instance.relations!.get(id)
        let relationIds: number[] = value.map(option => Number(option.value));
    
        if (oldRelations) {
            instance.deleteRelations!.set(id, oldRelations.filter(id => !relationIds.includes(id)));
        }
        instance.relations!.set(id, relationIds);
        setInstance((state) => {
            return {
            ...state,
            relations: instance.relations,
            deleteRelations: instance.deleteRelations
            };
        });
    }, [instance]);
    
    return {
        instance,
        setInstance,
        updateDataHandler,
        updateRelationHandler
    }
}