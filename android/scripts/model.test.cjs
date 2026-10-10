const test=require('node:test'),a=require('node:assert/strict');
let M;try{M=require('../app/src/main/assets/model.js')}catch{M={}}
test('legacy records preserve photos and valid saved coordinates',()=>{const t=M.normalizeTrip({id:'old',name:'바다',lat:37.75,lng:128.87,records:[{title:'카페',photos:['data:image/jpeg;base64,a'],cost:12000}]});a.equal(t.title,'바다');a.deepEqual(t.coord,[37.75,128.87]);a.equal(t.entries[0].photos[0],'data:image/jpeg;base64,a');a.equal(t.entries[0].expense,12000)});
test('unknown coordinates never become 0,0',()=>{a.equal(M.validCoord([null,null]),null);a.equal(M.validCoord([91,130]),null)});
test('planned trips excluded from visit stats; unrated ignored',()=>{const ts=[M.normalizeTrip({id:'1',region:'강원',city:'강릉시',entries:[{rating:5},{rating:0}]}),M.normalizeTrip({id:'2',region:'강원',city:'강릉시'}),M.normalizeTrip({id:'3',status:'planned',region:'제주',city:'제주시'})];const s=M.stats(ts);a.equal(s.visited,2);a.equal(s.cities,1);a.equal(s.regions,1);a.equal(s.rating,5)});
test('expenses sum without hidden conversion',()=>a.equal(M.totalExpense({entries:[{expense:12000},{expense:2500},{expense:0}]}),14500));
test('search includes entry text',()=>a.equal(M.matches({title:'여행',entries:[{body:'노을 사진'}]},'노을'),true));
test('reject reversed dates and malformed backup before writing',()=>{a.throws(()=>M.normalizeTrip({id:'x',startDate:'2026-10-02',endDate:'2026-10-01'}),/날짜/);a.throws(()=>M.prepareImport({trips:[{id:'ok',title:'정상'},null]}),/기록/);a.throws(()=>M.prepareImport({wrong:[]}),/형식/)});
test('import accepts old object and deduplicates IDs deterministically',()=>{const ts=M.prepareImport({trips:[{id:'x',title:'전'},{id:'x',title:'후'}]});a.equal(ts.length,1);a.equal(ts[0].title,'후')});
test('duplicate entry IDs cannot cause multi-record edits or deletion',()=>{const t=M.normalizeTrip({entries:[{id:'same',title:'첫째'},{id:'same',title:'둘째'}]});a.equal(t.entries.length,2);a.notEqual(t.entries[0].id,t.entries[1].id)});
test('import rejects days that cannot be reached in itinerary',()=>{for(const day of [367,Infinity,'Infinity'])a.throws(()=>M.prepareImport({trips:[{entries:[{day}]}]}),/일차/)});
