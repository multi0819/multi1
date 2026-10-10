(function(root){
'use strict';
const uid=()=>globalThis.crypto?.randomUUID?.()||`${Date.now()}-${Math.random().toString(36).slice(2)}`;
const str=v=>v==null?'':String(v);
const money=v=>Number.isFinite(Number(v))?Math.max(0,Math.round(Number(v)||0)):0;
function validCoord(v){if(!Array.isArray(v)||v.length!==2||v.some(x=>x===null||x===''||x===undefined))return null;const c=v.map(Number);return c.every(Number.isFinite)&&Math.abs(c[0])<=90&&Math.abs(c[1])<=180?c:null;}
function safeImage(v){return typeof v==='string'&&/^data:image\/(jpeg|jpg|png|webp|gif);base64,[a-zA-Z0-9+/=\s]+$/.test(v)?v:'';}
function date(v){if(!v)return '';const s=str(v).slice(0,10);if(!/^\d{4}-\d{2}-\d{2}$/.test(s)||!Number.isFinite(Date.parse(s))||new Date(s).toISOString().slice(0,10)!==s)throw Error('날짜 형식을 확인해 주세요.');return s;}
function normalizeTrip(raw){
 if(!raw||typeof raw!=='object'||Array.isArray(raw))throw Error('올바르지 않은 여행 기록입니다.');
 const startDate=date(raw.startDate||raw.start),endDate=date(raw.endDate||raw.end);
 if(startDate&&endDate&&endDate<startDate)throw Error('종료 날짜는 시작 날짜 이후여야 합니다.');
 const entries=raw.entries??raw.records??[];if(!Array.isArray(entries))throw Error('일정 기록 형식이 올바르지 않습니다.');
 const t={...raw,id:str(raw.id||uid()),title:str(raw.title||raw.name||raw.tripTitle||'제목 없는 여행'),country:raw.country==='해외'||raw.region==='해외'?'해외':'국내',region:str(raw.region||'서울'),city:str(raw.city||raw.sigungu||raw.cityName),startDate,endDate,note:str(raw.note||raw.memo||raw.description),cover:safeImage(raw.cover||raw.photo||raw.image),bookmarked:!!raw.bookmarked,status:raw.status==='planned'?'planned':'visited',budget:money(raw.budget),createdAt:raw.createdAt||Date.now(),updatedAt:raw.updatedAt||Date.now(),coord:validCoord(raw.coord)||validCoord([raw.lat,raw.lng]),entries:entries.map(e=>{
  if(!e||typeof e!=='object'||Array.isArray(e))throw Error('올바르지 않은 일정 기록입니다.');
  const day=Number(e.day||1);if(!Number.isFinite(day)||!Number.isInteger(day)||day<1||day>366)throw Error('여행 일차는 1~366 사이의 정수여야 합니다.');
  const photos=e.photos??(e.photo?[e.photo]:[]);if(!Array.isArray(photos))throw Error('사진 기록 형식이 올바르지 않습니다.');
  return {...e,id:str(e.id||uid()),day,time:/^([01]\d|2[0-3]):[0-5]\d$/.test(e.time)?e.time:'',type:['place','food','photo','memo'].includes(e.type)?e.type:'place',title:str(e.title||e.name||'기록'),body:str(e.body||e.content||e.memo||e.note),rating:Math.min(5,Math.max(0,Math.round(Number(e.rating)||0))),expense:money(e.expense??e.cost),photos:photos.map(safeImage).filter(Boolean)};
 })};const seen=new Set();for(const e of t.entries){if(seen.has(e.id))e.id=uid();seen.add(e.id);}return t;
}
function prepareImport(payload){const rows=Array.isArray(payload)?payload:payload?.trips??payload?.data??payload?.records;if(!Array.isArray(rows))throw Error('여행 백업 파일 형식이 아닙니다.');const byId=new Map();for(const row of rows){const t=normalizeTrip(row);byId.set(t.id,t);}return [...byId.values()];}
const totalExpense=t=>(t.entries||[]).reduce((s,e)=>s+money(e.expense),0);
const matches=(t,q)=>[t.title,t.region,t.city,t.note,...(t.entries||[]).flatMap(e=>[e.title,e.body])].join(' ').toLocaleLowerCase().includes(str(q).trim().toLocaleLowerCase());
function stats(trips){const visited=trips.filter(t=>t.status!=='planned'),ratings=visited.flatMap(t=>t.entries||[]).filter(e=>e.rating>0);return {visited:visited.length,planned:trips.length-visited.length,cities:new Set(visited.filter(t=>t.city).map(t=>`${t.country}/${t.region}/${t.city}`)).size,regions:new Set(visited.map(t=>`${t.country}/${t.region}`)).size,entries:trips.reduce((n,t)=>n+(t.entries||[]).length,0),photos:trips.reduce((n,t)=>n+(t.cover?1:0)+(t.entries||[]).reduce((n,e)=>n+(e.photos||[]).length,0),0),rating:ratings.length?ratings.reduce((n,e)=>n+e.rating,0)/ratings.length:0,expense:visited.reduce((n,t)=>n+totalExpense(t),0)};}
const api={uid,validCoord,safeImage,normalizeTrip,prepareImport,totalExpense,matches,stats};root.TravelModel=api;if(typeof module!=='undefined')module.exports=api;
})(typeof window==='undefined'?globalThis:window);
