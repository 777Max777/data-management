import { Type } from "./Field"
import { Entity } from "./Entity"

export interface ProtoInstance {
    id: number,
    name: string,
    entity: Entity,
    parent: ProtoInstance
}

export interface Instance {
    id?: number;
    name: string;
    data?: Map<number, string>;
    relations?: Map<number, number[]>;
    deleteRelations?: Map<number, number[]>;
}

export interface InstanceData extends Instance {
    value?: string | Instance[],
    fieldType: Type
}